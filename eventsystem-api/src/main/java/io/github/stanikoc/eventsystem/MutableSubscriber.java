package io.github.stanikoc.eventsystem;

/**
 * An extension of {@link Subscriber} that allows for dynamic registration of listeners.
 */
public interface MutableSubscriber extends Subscriber {
    /**
     * Overwrites the current array of listeners.
     *
     * @param listeners The new array of listeners.
     */
    void setListeners(EventListener<?>[] listeners);

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
    default void listen(EventListener<?> listener) {
        if (listener != null) {
            setListeners(Util.insert(getListeners(), listener));
        }
    }

    /**
     * Merges the listeners from another subscriber into this one.
     */
    default void listen(Subscriber subscriber) {
        if (subscriber != null && subscriber.getListeners() != null) {
            setListeners(Util.merge(getListeners(), subscriber.getListeners()));
        }
    }

}