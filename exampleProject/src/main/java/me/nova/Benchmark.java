package me.nova;

import me.nova.eventsystem.*;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class Benchmark {
    private static final TestEvent EVENT = new TestEvent();
    private static final EventBus EVENT_BUS = new EventBusImpl(new IdentityHashMap<>());

    public static long blackhole = 0;

    public static void main(String[] args) {
        benchmark1MillionEvents(5, false);
        benchmark200Listeners(5, false);

        benchmark1MillionEvents(10, true);
        benchmark200Listeners(10, true);
    }

    private static void benchmark1MillionEvents(int iterations, boolean print) {
        EventBus eventBus = EVENT_BUS;
        TestSubscriber subscriber = new TestSubscriber();
        eventBus.subscribe(subscriber);
        long totalTime = 0;
        for (int iter = 0; iter < iterations; iter++) {
            long start = System.nanoTime();
            for (int i = 0; i < 1_000_000; i++) {
                eventBus.post(EVENT);
            }

            long elapsed = System.nanoTime() - start;
            totalTime += elapsed;
        }

        eventBus.unsubscribe(subscriber);
        if (print) {
            double avgMs = (totalTime / (double) iterations) / 1_000_000.0;
            System.out.printf("Dispatch 1,000,000 events (1 Listener) : %.2f ms avg%n", avgMs);
        }
    }

    private static void benchmark200Listeners(int iterations, boolean print) {
        EventBus eventBus = EVENT_BUS;
        List<TestSubscriber> subscribers = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            TestSubscriber sub = new TestSubscriber();
            subscribers.add(sub);
            eventBus.subscribe(sub);
        }

        long totalTime = 0;
        int postsToAverage = 100_000;
        for (int iter = 0; iter < iterations; iter++) {
            long start = System.nanoTime();
            for (int i = 0; i < postsToAverage; i++) {
                eventBus.post(EVENT);
            }

            long elapsed = System.nanoTime() - start;
            totalTime += elapsed;
        }

        subscribers.forEach(eventBus::unsubscribe);
        if (print) {
            double avgNsPerDispatch = (totalTime / (double) iterations) / postsToAverage;
            System.out.printf("Dispatch 1 event (200 Listeners)       : %.2f ns avg%n", avgNsPerDispatch);
        }
    }

    public static final class TestSubscriber extends SubscriberImpl {
        public TestSubscriber() {
            listen(new Listener<TestEvent>() {
                @Override
                public void onEvent(TestEvent event) {
                    blackhole++;
                }
            });
        }
    }

    public record TestEvent() {}

}