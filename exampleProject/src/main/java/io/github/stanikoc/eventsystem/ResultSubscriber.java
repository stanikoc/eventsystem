package io.github.stanikoc.eventsystem;

public class ResultSubscriber extends SubscriberImpl {
    public ResultSubscriber() {
        listen(new Listener<ResultEvent>() {
            @Override
            public void onEvent(ResultEvent event) {
                System.out.println("The result is: " + event.result());
            }
        });

        listen(new EventListener<ResultEvent>() {
            @Override
            public void onEvent(ResultEvent event) {
                // Your logic
            }

            @Override
            public Class<ResultEvent> getType() {
                return ResultEvent.class;
            }
        });

        listen(ResultEvent.class, e -> {});
    }

    private static final class ResultListener extends Listener<ResultEvent> {
        @Override
        public void onEvent(ResultEvent event) {
            // Your logic here
        }
    }

}
