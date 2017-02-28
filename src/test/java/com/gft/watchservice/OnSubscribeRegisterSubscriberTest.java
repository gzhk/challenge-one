package com.gft.watchservice;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArraySet;

public final class OnSubscribeRegisterSubscriberTest {

    @Test
    public void addsSubscriberToTheSet() throws Exception {
        CopyOnWriteArraySet<Subscriber<? super Path>> subscribers = new CopyOnWriteArraySet<>();
        OnSubscribeRegisterSubscriber onSubscribeRegisterSubscriber = new OnSubscribeRegisterSubscriber(subscribers);

        TestSubscriber<Path> testSubscriber1 = new TestSubscriber<>();
        onSubscribeRegisterSubscriber.call(testSubscriber1);

        TestSubscriber<Path> testSubscriber2 = new TestSubscriber<>();
        onSubscribeRegisterSubscriber.call(testSubscriber2);

        Assertions.assertThat(subscribers).containsExactly(testSubscriber1, testSubscriber2);
    }
}