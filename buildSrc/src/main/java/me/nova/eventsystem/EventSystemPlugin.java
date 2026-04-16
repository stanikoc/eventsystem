package me.nova.eventsystem;

import me.nova.eventsystem.util.FileUtil;
import me.nova.eventsystem.util.InjectionUtil;
import me.nova.eventsystem.util.TaskUtil;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EventSystemPlugin implements Plugin<Project> {
    private static final String LISTENER_INTERFACE = "me.nova.eventsystem.EventListener";

    @Override
    public void apply(Project project) {
        project.getTasks().register("eventSystemInjector", task -> {
            Set<String> names = project.getTasks().getNames();
            Stream.of("classes", "testClasses")
                    .filter(names::contains)
                    .forEach(task::dependsOn);
            SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            sourceSets.configureEach(sourceSet -> {
                task.getInputs().files(sourceSet.getCompileClasspath());
                task.getInputs().files(sourceSet.getRuntimeClasspath());
            });
            task.doLast(t -> {
                try {
                    executeInjection(sourceSets);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        project.afterEvaluate(p -> {
            Set<String> taskNames = p.getTasks().getNames();
            TaskUtil.finalizeIfPresent(p, taskNames, "classes", "eventSystemInjector");
            TaskUtil.finalizeIfPresent(p, taskNames, "testClasses", "eventSystemInjector");
        });

        Set<String> taskNames = project.getTasks().getNames();
        TaskUtil.dependOnIfPresent(project, taskNames, "runClient", "eventSystemInjector");
        TaskUtil.dependOnIfPresent(project, taskNames, "runServer", "eventSystemInjector");
        TaskUtil.dependOnIfPresent(project, taskNames, "test", "eventSystemInjector");
        TaskUtil.dependOnIfPresent(project, taskNames, "jar", "eventSystemInjector");
    }

    private static void executeInjection(SourceSetContainer sourceSets) throws IOException {
        Set<File> allClasspath = sourceSets.stream()
                .flatMap(sourceSet -> Stream.of(sourceSet.getCompileClasspath().getFiles(), sourceSet.getRuntimeClasspath().getFiles(), sourceSet.getOutput().getClassesDirs().getFiles()))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        URL[] classpathUrls = allClasspath.stream().map(FileUtil::fileToUrl).toArray(URL[]::new);

        try (URLClassLoader loader = new URLClassLoader(classpathUrls, Thread.currentThread().getContextClassLoader())) {
            List<File> listenerFiles = new ArrayList<>();
            Class<?> listenerTargetClass = loader.loadClass(LISTENER_INTERFACE);

            sourceSets.stream()
                    .flatMap(sourceSet -> sourceSet.getOutput().getClassesDirs().getFiles().stream())
                    .filter(File::exists)
                    .flatMap(dir -> FileUtil.walk(dir.toPath()))
                    .map(Path::toFile)
                    .filter(file -> file.isFile() && file.getName().endsWith(".class"))
                    // Inside EventSystemPlugin.java
                    .forEach(file -> {
                        String name = FileUtil.getClassName(file);
                        if (name.equals("me.nova.eventsystem.Listener")) {
                            InjectionUtil.stripBaseListener(file, loader);
                            return;
                        }

                        if (name.contains("$")) {
                            return;
                        }

                        try {
                            Class<?> clazz = loader.loadClass(name);
                            if (listenerTargetClass.isAssignableFrom(clazz) &&
                                    !clazz.isInterface() &&
                                    !Modifier.isAbstract(clazz.getModifiers())) {
                                listenerFiles.add(file);
                            }
                        } catch (Throwable ignored) {}
                    });

            listenerFiles.forEach(file -> {
                Class<?> clazz = FileUtil.loadClass(loader, file);
                TypeHelper.TypeInfo<?> typeInfo = TypeHelper.findTypeInfo(clazz, listenerTargetClass);
                InjectionUtil.injectListenerTypes(file, loader, typeInfo.type(), typeInfo.generic());
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}