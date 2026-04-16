package me.nova.eventsystem;

import java.util.Arrays;

final class Util {
    private Util() throws IllegalAccessException {
        throw new IllegalAccessException("This is a utility class and cannot be instantiated.");
    }

    static <E> EventListener<E> instantiate(Class<E> type, Class<?> genericType, int priority, EventConsumer<E> consumer) {
        return new EventListener<E>() {
            @Override
            public void onEvent(E event) {
                consumer.onEvent(event);
            }

            @Override
            public Class<E> getType() {
                return type;
            }

            @Override
            public Class<?> getGenericType() {
                return genericType;
            }

            @Override
            public int getPriority() {
                return priority;
            }
        };
    }

    static EventListener<?>[] insert(EventListener<?>[] a, EventListener<?> e) {
        if (e == null) {
            return a != null ? a : new EventListener<?>[0];
        }

        if (a == null || a.length == 0) {
            return new EventListener<?>[]{e};
        }

        int l = a.length;
        EventListener<?>[] n = Arrays.copyOf(a, l + 1);
        int i = 0;

        while (i < l && e.compareTo(a[i]) < 0) {
            i++;
        }

        if (i < l) {
            System.arraycopy(a, i, n, i + 1, l - i);
        }

        n[i] = e;
        return n;
    }

    static EventListener<?>[] remove(EventListener<?>[] a, EventListener<?> e) {
        if (a == null || a.length == 0) {
            return a;
        }

        int l = a.length;
        int index = -1;
        for (int i = 0; i < l; i++) {
            if (a[i].equals(e)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return a;
        }

        EventListener<?>[] newArray = Arrays.copyOf(a, l - 1);
        if (index < l - 1) {
            System.arraycopy(a, index + 1, newArray, index, l - index - 1);
        }

        return newArray;
    }

    static EventListener<?>[] merge(EventListener<?>[] c, EventListener<?>[] a) {
        if (a == null || a.length == 0) {
            return c != null ? c : new EventListener<?>[0];
        }

        if (c == null || c.length == 0) {
            EventListener<?>[] next = new EventListener<?>[a.length];
            System.arraycopy(a, 0, next, 0, a.length);
            return next;
        }

        EventListener<?>[] next = new EventListener<?>[c.length + a.length];
        System.arraycopy(c, 0, next, 0, c.length);
        System.arraycopy(a, 0, next, c.length, a.length);
        return next;
    }

}
