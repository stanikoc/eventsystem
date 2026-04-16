package io.github.stanikoc.eventsystem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A central dispatching system for broadcasting events to registered listeners.
 */
public interface EventBus {
    /**
     * Dispatches an event to all registered listeners.
     * @param event The event payload to dispatch.
     */
    void post(@NotNull Object event);

    /**
     * Dispatches an event, strictly filtering listeners by a specific generic type.
     * <p>
     * Listeners receive the event only if their declared generic type matches the
     * provided type, or if they do not declare a generic constraint.
     * @param event The event payload to dispatch.
     * @param type  The generic class type to filter by, or {@code null} to bypass filtering.
     */
    void post(@NotNull Object event, @Nullable Class<?> type);

    /**
     * Registers all listeners provided by the given subscriber.
     * @param subscriber The subscriber containing the listeners to register.
     */
    void subscribe(@NotNull Subscriber subscriber);

    /**
     * Unregisters all listeners provided by the given subscriber.
     * @param subscriber The subscriber containing the listeners to unregister.
     */
    void unsubscribe(@NotNull Subscriber subscriber);

    /**
     * Registers a single listener to the event bus.
     * @param listener The listener to register.
     */
    void register(@NotNull EventListener<?> listener);

    /**
     * Unregisters a single listener from the event bus.
     * @param listener The listener to unregister.
     */
    void unregister(@NotNull EventListener<?> listener);

    /**
     * Checks if the given subscriber is currently registered with the event bus.
     * @param subscriber The subscriber to check.
     * @return {@code true} if the subscriber is registered, {@code false} otherwise.
     */
    boolean isSubscribed(@NotNull Subscriber subscriber);

}