package com.gft.watchservice;

import org.jetbrains.annotations.NotNull;
import rx.Observer;
import rx.Subscriber;

import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Notify subscribers about Paths polled from WatchServicePaths.
 * Removes subscriber from Set if it is unsubscribed.
 */
public final class NotifySubscribers implements Runnable {

    private final WatchService watchService;
    private final CopyOnWriteArrayList<Subscriber<? super Path>> subscribers;

    public NotifySubscribers(
        @NotNull final WatchService watchService,
        @NotNull final CopyOnWriteArrayList<Subscriber<? super Path>> subscribers
    ) {
        this.watchService = watchService;
        this.subscribers = subscribers;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<Path> paths = PollWatchServicePaths.poll(watchService);
                RegistersPaths.register(paths.stream(), watchService);
                paths.forEach(path ->
                    subscribers
                        .stream()
                        .filter(subscriber -> !subscriber.isUnsubscribed())
                        .forEach(subscriber -> subscriber.onNext(path))
                );
            } catch (CouldNotReadRootPath | CouldNotRegisterPath e) {
                // We don't want to kill application because of this exceptions
            }
        }

        subscribers
            .stream()
            .filter(subscriber -> !subscriber.isUnsubscribed())
            .forEach(Observer::onCompleted);
    }
}
