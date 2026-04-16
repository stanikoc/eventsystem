package io.github.stanikoc.eventsystem.util;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class FileUtil {
    private FileUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is a utility class and cannot be instantiated.");
    }

    public static Stream<Path> walk(Path start, FileVisitOption... options) {
        try {
            return Files.walk(start, Integer.MAX_VALUE, options);
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    public static URL fileToUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getClassName(File file) {
        try {
            return new ClassReader(Files.readAllBytes(file.toPath())).getClassName().replace('/', '.');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> loadClass(ClassLoader loader, File file) {
        try {
            return loader.loadClass(getClassName(file));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
