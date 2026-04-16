package me.nova;

import me.nova.eventsystem.Listener;
import me.nova.eventsystem.SubscriberImpl;

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
