package io.github.stanikoc.eventsystem;

public class ResultListener extends SubscriberImpl {
    public ResultListener() {
        listen(new Listener<ResultEvent>() {
            @Override
            public void onEvent(ResultEvent event) {
                System.out.println("The result is: " + event.result());
            }
        });
    }

}
