package me.nova.eventsystem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;

class EventBusImplTest {
    private final EventBusImpl eventBus = new EventBusImpl();

    @Test
    void testRegisterAndPost() {
        AtomicInteger callCount = new AtomicInteger(0);
        EventListener<TestEvent> listener = instantiate(null, e -> callCount.incrementAndGet());

        eventBus.register(listener);
        eventBus.post(new TestEvent());

        assertEquals(1, callCount.get(), "Listener should be called exactly once.");
    }

    @Test
    void testUnregister() {
        AtomicInteger callCount = new AtomicInteger(0);
        EventListener<TestEvent> listener = instantiate(null, e -> callCount.incrementAndGet());

        eventBus.register(listener);
        eventBus.unregister(listener);
        eventBus.post(new TestEvent());

        assertEquals(0, callCount.get(), "Listener should not be called after unregistering.");
    }

    @Test
    void testGenericTypeFiltering() {
        AtomicInteger specificCall = new AtomicInteger(0);
        AtomicInteger generalCall = new AtomicInteger(0);

        eventBus.register(instantiate(String.class, e -> specificCall.incrementAndGet()));
        eventBus.register(instantiate(null, e -> generalCall.incrementAndGet()));
        eventBus.post(new TestEvent());
        eventBus.post(new TestEvent(), Integer.class);

        assertEquals(1, specificCall.get(), "Specific listener should only trigger on matching or null filter.");
        assertEquals(2, generalCall.get(), "General listener should always trigger.");
    }

    private static EventListener<TestEvent> instantiate(Class<?> generic, EventConsumer<TestEvent> consumer) {
        return new EventListener<>() {
            @Override
            public void onEvent(TestEvent event) {
                consumer.onEvent(event);
            }

            @Override
            public Class<TestEvent> getType() {
                return TestEvent.class;
            }

            @Override
            public Class<?> getGenericType() {
                return generic;
            }
        };
    }
}