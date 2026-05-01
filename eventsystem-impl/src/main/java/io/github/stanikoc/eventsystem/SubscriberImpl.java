package io.github.stanikoc.eventsystem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SubscriberImpl implements Subscriber {
    private final List<EventListener<?>> listeners = new ArrayList<>();

    @Override
    public @NotNull List<EventListener<?>> getListeners() {
        return listeners;
    }

}
