package io.github.stanikoc.eventsystem;

public class SubscriberImpl implements MutableSubscriber {
    private EventListener<?>[] listeners = null;

    @Override
    public final EventListener<?>[] getListeners() {
        return listeners;
    }

    @Override
    public final void setListeners(EventListener<?>[] listeners) {
        this.listeners = listeners;
    }

}
