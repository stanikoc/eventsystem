package me.nova.eventsystem;

/**
 * A prioritized consumer bound to a specific event type.
 * @param <E> The type of the event this listener consumes.
 */
public interface EventListener<E> extends EventConsumer<E>, Comparable<EventListener<?>> {
    int DEFAULT_LISTENER_PRIORITY = 0;

    /**
     * Gets the primary event class type this listener requires.
     * @return The event class type.
     */
    Class<E> getType();

    /**
     * Gets the specific generic class type this listener requires.
     * <p>
     * Used by the event bus to filter events that share a base class but carry different payloads.
     * @return The specific generic type class, or {@code null} if no strict typing is required.
     */
    default Class<?> getGenericType() {
        return null;
    }

    /**
     * Gets the execution priority of this listener.
     * <p>
     * Listeners with higher priority values are executed before those with lower values.
     * @return The priority level.
     */
    default int getPriority() {
        return DEFAULT_LISTENER_PRIORITY;
    }

    @Override
    default int compareTo(EventListener<?> o) {
        return Integer.compare(this.getPriority(), o.getPriority());
    }

}