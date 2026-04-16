# EventSystem

A blazing-fast event bus for Java.

## The Magic: Type-Aware Listeners

Traditionally, subscribing to events in Java requires clunky boilerplate where you must explicitly pass the class type you want to listen to. EventSystem changes the game. 

By leveraging our compile-time injector, you simply create an anonymous `Listener<T>` and the system *automatically* knows what you are listening to. No `.class` parameters, no reflection, just clean and compact code:

```java
// Traditional systems:
// listen(ResultEvent.class, event -> { ... });

// EventSystem:
listen(new Listener<ResultEvent>() { 
    @Override
    public void onEvent(ResultEvent event) {
        // We already know it's a ResultEvent. Blazing fast!
    }
});
```

## Requirements
Java 17 or higher

## Installation

In your build.gradle:

```gradle
plugins {
    // Required if you want to use Listener object.
    id 'io.github.stanikoc.eventsystem.injector' version '1.0.0'
}

dependencies {
    implementation 'io.github.stanikoc:eventsystem-impl:1.0.0'
}
```

## Quick Start
Getting started takes only a few lines of code.

### 1. Define an Event

```java
package com.example;

// It can be any object, but records are preferred due to a better lookup time.
public record ResultEvent(long result) {} //
```
### 2. Create a Subscriber
Create a class that extends SubscriberImpl. That class holds all the listeners.

```java
package com.example;

import io.github.stanikoc.eventsystem.SubscriberImpl;
import io.github.stanikoc.eventsystem.Listener;

public class ResultListener extends SubscriberImpl { //
    public ResultListener() {
        // The generic type <ResultEvent> is automatically resolved at compile time
        listen(new Listener<ResultEvent>() { //
            @Override
            public void onEvent(ResultEvent event) { //
                System.out.println("The result is: " + event.result()); //
            }
        });

        // Alternatively, one may make a normal class, not necessarily an anonymous one.
        listen(new ResultListener());

        // Or use a lambda method
        listen(ResultEvent.class, e -> {});
    }

    private static final class ResultListener extends Listener<ResultEvent> {
        @Override
        public void onEvent(ResultEvent event) {
            // Your logic here
        }
    }
    
}
```

### 3. Register and Post
Instantiate your EventBus, register your subscriber, and start dispatching events.

```java
package com.example;

import io.github.stanikoc.eventsystem.EventBus;
import io.github.stanikoc.eventsystem.EventBusImpl;

public class Main {
    private static final EventBus eventBus = new EventBusImpl(); //

    public static void main(String[] args) {
        // Register the subscriber
        eventBus.subscribe(new ResultListener()); //

        // Post an event to all active listeners!
        eventBus.post(new ResultEvent(42)); //
    }
}
```

