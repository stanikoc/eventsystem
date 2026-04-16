package me.nova.eventsystem;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public final class TypeHelper {
    private TypeHelper() throws IllegalAccessException {
        throw new IllegalAccessException("This is a utility class and cannot be instantiated.");
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeInfo<T> findTypeInfo(Class<?> startClass, Class<?> targetClass) {
        Type supertype = findSupertype(startClass, targetClass);
        if (supertype instanceof ParameterizedType pt) {
            Type[] args = pt.getActualTypeArguments();
            if (args.length > 0) {
                return (TypeInfo<T>) extractTypeInfo(args[0]);
            }
        }

        throw new RuntimeException("Cannot find generic type info for " + targetClass.getSimpleName() + " on class " + startClass.getName());
    }

    private static Type findSupertype(Type type, Class<?> targetClass) {
        if (type == null) {
            return null;
        }

        Class<?> raw = getRawClass(type);
        if (raw == null)  {
            return null;
        }

        if (raw.equals(targetClass)) {
            return type;
        }

        Type genericSuper = raw.getGenericSuperclass();
        if (genericSuper != null) {
            Type resolved = resolve(genericSuper, type);
            Type result = findSupertype(resolved, targetClass);
            if (result != null) {
                return result;
            }
        }

        for (Type iface : raw.getGenericInterfaces()) {
            Type resolved = resolve(iface, type);
            Type result = findSupertype(resolved, targetClass);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private static Type resolve(Type toResolve, Type contextType) {
        if (!(contextType instanceof ParameterizedType contextPt)) {
            return toResolve;
        }

        Class<?> rawContext = (Class<?>) contextPt.getRawType();
        TypeVariable<?>[] params = rawContext.getTypeParameters();
        Type[] args = contextPt.getActualTypeArguments();
        Map<TypeVariable<?>, Type> typeVarMap = new HashMap<>();
        for (int i = 0; i < params.length; i++) {
            typeVarMap.put(params[i], args[i]);
        }

        return resolveWithTypeVarMap(toResolve, typeVarMap);
    }

    private static Type resolveWithTypeVarMap(Type type, Map<TypeVariable<?>, Type> typeVarMap) {
        if (type instanceof TypeVariable<?> tv) {
            return typeVarMap.getOrDefault(tv, tv);
        }

        if (type instanceof ParameterizedType pt) {
            Type[] oldArgs = pt.getActualTypeArguments();
            Type[] newArgs = new Type[oldArgs.length];
            boolean changed = false;
            for (int i = 0; i < oldArgs.length; i++) {
                newArgs[i] = resolveWithTypeVarMap(oldArgs[i], typeVarMap);
                if (newArgs[i] != oldArgs[i]) {
                    changed = true;
                }
            }

            if (changed) {
                return new ParameterizedTypeImpl((Class<?>) pt.getRawType(), newArgs, pt.getOwnerType());
            }
        }

        return type;
    }

    private static Class<?> getRawClass(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz;
        }

        if (type instanceof ParameterizedType pt) {
            return (Class<?>) pt.getRawType();
        }

        if (type instanceof TypeVariable<?> tv) {
            Type[] bounds = tv.getBounds();
            return bounds.length > 0 ? getRawClass(bounds[0]) : Object.class;
        }

        return null;
    }

    private static TypeInfo<?> extractTypeInfo(Type type) {
        Class<?> primary = getRawClass(type);
        Class<?> secondary = null;
        if (type instanceof ParameterizedType pt) {
            Type[] nestedArgs = pt.getActualTypeArguments();
            if (nestedArgs.length > 0) {
                Type nestedArg = nestedArgs[0];
                if (nestedArg instanceof WildcardType wt && wt.getUpperBounds().length > 0 && wt.getUpperBounds()[0] != Object.class) {
                    secondary = getRawClass(wt.getUpperBounds()[0]);
                } else {
                    secondary = getRawClass(nestedArg);
                }
            }
        }

        return new TypeInfo<>(primary, secondary);
    }

    public record TypeInfo<T>(Class<T> type, Class<?> generic) {}

    private record ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) implements ParameterizedType {
        @Override
        public Type @NotNull [] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        @Override
        public @NotNull Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }
    }

}