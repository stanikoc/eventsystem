package me.nova.eventsystem.util;

import me.nova.eventsystem.BaseListenerStripper;
import me.nova.eventsystem.ListenerTypeInjector;
import me.nova.eventsystem.TypeResolver;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Function;

public final class InjectionUtil {
    private InjectionUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is a utility class and cannot be instantiated.");
    }

    public static void stripBaseListener(File file, ClassLoader loader) {
        try {
            modifyClass(file, loader, BaseListenerStripper::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void injectListenerTypes(File file, ClassLoader loader, Class<?> primary, Class<?> secondary) {
        try {
            modifyClass(file, loader, writer -> new ListenerTypeInjector(writer, primary, secondary));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void modifyClass(File file, ClassLoader loader, Function<ClassWriter, ClassVisitor> visitorProvider) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new TypeResolver(reader, ClassWriter.COMPUTE_FRAMES, loader);

        reader.accept(visitorProvider.apply(writer), 0);
        Files.write(file.toPath(), writer.toByteArray());
    }

}