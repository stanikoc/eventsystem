package io.github.stanikoc.eventsystem;

import java.util.ArrayList;
import java.util.List;

public class Benchmark {
    private static final TestEvent EVENT = new TestEvent();
    private static final EventBus EVENT_BUS = new EventBusImpl();

    public static long blackhole = 0;

    public static void main(String[] args) {
        // Warmup phase
        benchmark1MillionEvents(5, false);
        benchmark5Listeners(5, false);
        benchmark200Listeners(5, false);
        benchmarkSubscribeUnsubscribe(5, false);

        // Measurement phase
        benchmark1MillionEvents(10, true);
        benchmark5Listeners(10, true);
        benchmark200Listeners(10, true);
        benchmarkSubscribeUnsubscribe(10, true);
    }

    private static void benchmarkSubscribeUnsubscribe(int iterations, boolean print) {
        long totalSubTime = 0;
        long totalUnsubTime = 0;
        int subsPerIter = 200;

        for (int iter = 0; iter < iterations; iter++) {
            List<TestSubscriber> subscribers = new ArrayList<>();
            for (int i = 0; i < subsPerIter; i++) {
                subscribers.add(new TestSubscriber());
            }

            // Measure Subscribe
            long startSub = System.nanoTime();
            for (int i = 0; i < subsPerIter; i++) {
                EVENT_BUS.subscribe(subscribers.get(i));
            }

            long endSub = System.nanoTime();
            totalSubTime += (endSub - startSub);

            // Measure Unsubscribe
            long startUnsub = System.nanoTime();
            for (int i = 0; i < subsPerIter; i++) {
                EVENT_BUS.unsubscribe(subscribers.get(i));
            }

            long endUnsub = System.nanoTime();
            totalUnsubTime += (endUnsub - startUnsub);
        }

        if (print) {
            double avgSubNs = (double) totalSubTime / (iterations * subsPerIter);
            double avgUnsubNs = (double) totalUnsubTime / (iterations * subsPerIter);

            System.out.printf("Subscribe (avg per listener)           : %.4f ms%n", avgSubNs / 1_000_000.0);
            System.out.printf("Unsubscribe (avg per listener)         : %.4f ms%n", avgUnsubNs / 1_000_000.0);
        }
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
            System.out.printf("Dispatch 1,000,000 events (1 Listener)  : %.4f ms avg%n", avgMs);
        }
    }

    private static void benchmark5Listeners(int iterations, boolean print) {
        EventBus eventBus = EVENT_BUS;
        List<TestSubscriber> subscribers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TestSubscriber sub = new TestSubscriber();
            subscribers.add(sub);
            eventBus.subscribe(sub);
        }

        long totalTime = 0;
        int postsToAverage = 1_000_000;

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
            double avgMs = (totalTime / (double) iterations) / 1_000_000.0;
            System.out.printf("Dispatch 1,000,000 events (5 Listeners)  : %.4f ms avg%n", avgMs);
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
                    blackhole ^= 1; // Forces JIT to process the data
                }
            });
        }
    }

    public record TestEvent() {}

}