package io.github.stanikoc.eventsystem;

/**
 * A provider of event listeners.
 */
@FunctionalInterface
public interface Subscriber {
    /**
     * Gets the array of listeners maintained by this subscriber.
     * @return An array of listeners, or {@code null} if none exist.
     */
    EventListener<?>[] getListeners();

}