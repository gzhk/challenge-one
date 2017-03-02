package com.gft.watchservice;

import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;

import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public final class OnSubscribeRegisterSubscriber implements Observable.OnSubscribe<Path> {

    private final CopyOnWriteArrayList<Subscriber<? super Path>> subscribers;

    public OnSubscribeRegisterSubscriber(@NotNull final CopyOnWriteArrayList<Subscriber<? super Path>> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public void call(Subscriber<? super Path> subscriber) {
        subscribers.add(subscriber);
    }
}
