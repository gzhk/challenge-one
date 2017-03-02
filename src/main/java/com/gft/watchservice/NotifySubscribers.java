package com.gft.watchservice;

import org.jetbrains.annotations.NotNull;
import rx.Subscriber;

import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Notify subscribers about Paths polled from WatchServicePaths.
 * Removes subscriber from Set if it is unsubscribed.
 */
public final class NotifySubscribers implements Runnable {

    private final CopyOnWriteArrayList<Subscriber<? super Path>> subscribers;
    private final WatchServicePaths watchServicePaths;

    public NotifySubscribers(
        @NotNull final CopyOnWriteArrayList<Subscriber<? super Path>> subscribers,
        @NotNull final WatchServicePaths watchServicePaths
    ) {
        this.subscribers = subscribers;
        this.watchServicePaths = watchServicePaths;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Path path : watchServicePaths.poll()) {
                    subscribers
                        .stream()
                        .filter(subscriber -> {
                            if (subscriber.isUnsubscribed()) {
                                subscribers.remove(subscriber);

                                return false;
                            }

                            return true;
                        })
                        .forEach(subscriber -> subscriber.onNext(path));
                }
            } catch (Throwable e) {
                subscribers
                    .stream()
                    .filter(subscriber -> !subscriber.isUnsubscribed())
                    .forEach(subscriber -> subscriber.onError(e));

                return;
            }
        }
    }
}
