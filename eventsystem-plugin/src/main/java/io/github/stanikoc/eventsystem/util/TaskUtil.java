package io.github.stanikoc.eventsystem.util;

import org.gradle.api.Project;

import java.util.Set;

public final class TaskUtil {
    private TaskUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is a utility class and cannot be instantiated.");
    }

    public static void dependOnIfPresent(Project project, Set<String> names, String task, String dependency) {
        if (names.contains(task)) {
            project.getTasks().named(task, t -> t.dependsOn(dependency));
        }
    }

    public static void finalizeIfPresent(Project project, Set<String> names, String task, String finalizer) {
        if (names.contains(task)) {
            project.getTasks().named(task, t -> t.finalizedBy(finalizer));
        }
    }

}