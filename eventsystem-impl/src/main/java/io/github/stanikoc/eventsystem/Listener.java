package io.github.stanikoc.eventsystem;

public abstract class Listener<E> implements EventListener<E> {
    private final int priority;

    public Listener() {
        this(DEFAULT_LISTENER_PRIORITY);
    }

    public Listener(int priority) {
        this.priority = priority;
    }

    @Override
    public final Class<E> getType() {
        throw new RuntimeException("This method was supposed to be overridden at compile time.");
    }

    @Override
    public final Class<?> getGenericType() {
        throw new RuntimeException("This method was supposed to be overridden at compile time.");
    }

    @Override
    public final int getPriority() {
        return priority;
    }

}
