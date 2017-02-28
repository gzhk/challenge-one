package com.gft.watchservice;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class NotifySubscribersTest {

    @Test
    public void passesPathToAllSubscribers() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        Path subPath = rootPath.resolve("subPath");
        Files.createDirectory(subPath);

        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();
        TestSubscriber<Path> testSubscriber2 = new TestSubscriber<>();

        CopyOnWriteArraySet<Subscriber<? super Path>> subscribers = new CopyOnWriteArraySet<>();
        subscribers.add(testSubscriber);
        subscribers.add(testSubscriber2);

        NotifySubscribers notifySubscribers = new NotifySubscribers(
            subscribers,
            new WatchServicePaths() {

                private int pollPathsCalls = 0;

                @NotNull
                @Override
                public List<Path> poll() {
                    return pollPathsCalls++ > 0 ? new ArrayList<>() : Arrays.asList(rootPath, subPath);
                }
            }
        );

        Executors.newSingleThreadExecutor().submit(notifySubscribers);

        testSubscriber.awaitValueCount(2, 10, TimeUnit.SECONDS);
        testSubscriber.assertReceivedOnNext(Arrays.asList(rootPath, subPath));

        testSubscriber2.awaitValueCount(2, 10, TimeUnit.SECONDS);
        testSubscriber2.assertReceivedOnNext(Arrays.asList(rootPath, subPath));
    }

    @Test
    public void removesSubscriberFromSetWhenItIsUnSubscribed() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();
        testSubscriber.unsubscribe();

        CopyOnWriteArraySet<Subscriber<? super Path>> subscribers = new CopyOnWriteArraySet<>();
        subscribers.add(testSubscriber);

        NotifySubscribers notifySubscribers = new NotifySubscribers(
            subscribers,
            new WatchServicePaths() {

                private int pollPathsCalls = 0;

                @NotNull
                @Override
                public List<Path> poll() {
                    if (pollPathsCalls++ > 0) {
                        throw new RuntimeException("break loop");
                    }

                    return Collections.singletonList(rootPath);
                }
            }
        );

        notifySubscribers.run();

        Assertions.assertThat(subscribers).isEmpty();
        testSubscriber.assertNoValues();
    }

    @Test
    public void reportExceptionToSubscribers() throws Exception {
        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();

        CopyOnWriteArraySet<Subscriber<? super Path>> subscribers = new CopyOnWriteArraySet<>();
        subscribers.add(testSubscriber);

        NotifySubscribers notifySubscribers = new NotifySubscribers(
            subscribers,
            () -> {
                throw new NullPointerException();
            }
        );

        notifySubscribers.run();

        testSubscriber.assertError(NullPointerException.class);
    }

    @Test
    public void doesNotReportErrorIfSubscriberIsUnSubscribed() throws Exception {
        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();
        testSubscriber.unsubscribe();

        CopyOnWriteArraySet<Subscriber<? super Path>> subscribers = new CopyOnWriteArraySet<>();
        subscribers.add(testSubscriber);

        NotifySubscribers notifySubscribers = new NotifySubscribers(
            subscribers,
            () -> {
                throw new NullPointerException();
            }
        );

        notifySubscribers.run();

        testSubscriber.assertNoValues();
        testSubscriber.assertNoErrors();
    }
}
