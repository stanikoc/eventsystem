package io.github.stanikoc.eventsystem;

/**
 * Represents an operation that accepts a single event payload.
 * @param <E> The type of the event being consumed.
 */
@FunctionalInterface
public interface EventConsumer<E> {
    /**
     * Performs this operation on the given event.
     * @param event The event payload.
     */
    void onEvent(E event);

}