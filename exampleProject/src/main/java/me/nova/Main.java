package me.nova;

import me.nova.eventsystem.EventBus;
import me.nova.eventsystem.EventBusImpl;

public class Main {
    private static final EventBus eventBus = new EventBusImpl();

    public static void main(String[] args) {
        // Subscribing the listener so it starts receiving events.
        eventBus.subscribe(new ResultListener());

        long timeMillis = System.currentTimeMillis();
        long result = timeMillis % 300;
        // Dispatching the event to all listeners.
        eventBus.post(new ResultEvent(result));
    }

}