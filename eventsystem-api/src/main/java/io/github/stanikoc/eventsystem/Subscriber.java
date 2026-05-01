package io.github.stanikoc.eventsystem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A provider of event listeners.
 */
@FunctionalInterface
public interface Subscriber {
    /**
     * Gets the array of listeners maintained by this subscriber.
     * @return An array of listeners, or {@code null} if none exist.
     */
    @NotNull List<EventListener<?>> getListeners();

    /**
     * Binds a consumer to an event type with default priority.
     */
    default <E> void listen(Class<E> type, EventConsumer<E> consumer) {
        listen(type, EventListener.DEFAULT_LISTENER_PRIORITY, consumer);
    }

    /**
     * Binds a consumer to an event type with a specific priority.
     */
    default <E> void listen(Class<E> type, int priority, EventConsumer<E> consumer) {
        listen(type, null, priority, consumer);
    }

    /**
     * Binds a consumer to an event type and a generic subtype with default priority.
     */
    default <E> void listen(Class<E> type, Class<?> genericType, EventConsumer<E> consumer) {
        listen(type, genericType, EventListener.DEFAULT_LISTENER_PRIORITY, consumer);
    }

    /**
     * Binds a consumer to an event type and a generic subtype with a specific priority.
     */
    default <E> void listen(Class<E> type, Class<?> genericType, int priority, EventConsumer<E> consumer) {
        listen(Util.instantiate(type, genericType, priority, consumer));
    }

    /**
     * Appends a pre-configured listener to this subscriber.
     */
    default void listen(@NotNull EventListener<?> listener) {
        getListeners().add(listener);
    }

    /**
     * Merges the listeners from another subscriber into this one.
     */
    default void listen(@NotNull Subscriber subscriber) {
        getListeners().addAll(subscriber.getListeners());
    }

}