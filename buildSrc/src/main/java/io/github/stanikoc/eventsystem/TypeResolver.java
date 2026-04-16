package io.github.stanikoc.eventsystem;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public final class TypeResolver extends ClassWriter {
    private final ClassLoader loader;

    public TypeResolver(ClassReader classReader, int flags, ClassLoader loader) {
        super(classReader, flags);
        this.loader = loader;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        try {
            Class<?> c = Class.forName(type1.replace('/', '.'), false, loader);
            Class<?> d = Class.forName(type2.replace('/', '.'), false, loader);
            if (c.isAssignableFrom(d)) {
                return type1;
            } else if (d.isAssignableFrom(c)) {
                return type2;
            } else if (c.isInterface() || d.isInterface())  {
                return "java/lang/Object";
            }

            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        } catch (Throwable ignored) {
            return "java/lang/Object";
        }
    }

}