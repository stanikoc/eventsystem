package io.github.stanikoc.eventsystem;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class EventBusImpl implements EventBus {
    private final Map<Class<?>, EventListener<?>[]> lookupByClass;
    private final Set<Subscriber> subscribers;

    public EventBusImpl() {
        this(new ConcurrentHashMap<>());
    }

    public EventBusImpl(Map<Class<?>, EventListener<?>[]> lookupByClass) {
        this(lookupByClass, ConcurrentHashMap.newKeySet());
    }

    private EventBusImpl(Map<Class<?>, EventListener<?>[]> lookupByClass, Set<Subscriber> subscribers) {
        this.lookupByClass = lookupByClass;
        this.subscribers = subscribers;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void post(@NotNull Object event) {
        EventListener[] listeners = lookupByClass.get(event.getClass());
        if (listeners != null) {
            for (EventListener listener : listeners) {
                listener.onEvent(event);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void post(@NotNull Object event, Class<?> type) {
        EventListener[] listeners = lookupByClass.get(event.getClass());
        if (listeners != null) {
            for (EventListener listener : listeners) {
                Class<?> genericType = listener.getGenericType();
                if (genericType == null || genericType == type) {
                    listener.onEvent(event);
                }
            }
        }
    }

    @Override
    public void subscribe(@NotNull Subscriber subscriber) {
        if (subscribers.add(subscriber)) {
            EventListener<?>[] listeners = subscriber.getListeners();
            if (listeners != null) {
                for (EventListener<?> l : listeners) {
                    register(l);
                }
            }
        }
    }

    @Override
    public void unsubscribe(@NotNull Subscriber subscriber) {
        if (subscribers.remove(subscriber)) {
            EventListener<?>[] listeners = subscriber.getListeners();
            if (listeners != null) {
                for (EventListener<?> l : listeners) {
                    unregister(l);
                }
            }
        }
    }

    @Override
    public void register(@NotNull EventListener<?> listener) {
        lookupByClass.compute(listener.getType(), (eventType, currentListeners) ->
                Util.insert(currentListeners, listener));
    }

    @Override
    public void unregister(@NotNull EventListener<?> listener) {
        lookupByClass.compute(listener.getType(), (eventType, currentListeners) -> {
            if (currentListeners == null || currentListeners.length == 0) {
                return null;
            }

            EventListener<?>[] n = Util.remove(currentListeners, listener);
            return (n == null || n.length == 0) ? null : n;
        });
    }

    @Override
    public boolean isSubscribed(@NotNull Subscriber subscriber) {
        return subscribers.contains(subscriber);
    }

}
