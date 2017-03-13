package com.gft.watchservice;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.sun.nio.file.SensitivityWatchEventModifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.*;

public final class NotifySubscribersTest {

    @Test
    public void passesPathToAllSubscribers() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        TestSubscriber<Path> testSubscriber1 = new TestSubscriber<>();
        TestSubscriber<Path> testSubscriber2 = new TestSubscriber<>();

        CopyOnWriteArrayList<Subscriber<? super Path>> subscribers = new CopyOnWriteArrayList<>();
        subscribers.add(testSubscriber1);
        subscribers.add(testSubscriber2);

        NotifySubscribers notifySubscribers = new NotifySubscribers(watchService, subscribers);
        Executors.newSingleThreadExecutor().submit(notifySubscribers);

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        rootPath.register(
            watchService,
            new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE},
            SensitivityWatchEventModifier.HIGH
        );

        Path subPath = rootPath.resolve("subPath");
        Files.createDirectory(subPath);

        testSubscriber1.awaitValueCount(1, 10, TimeUnit.SECONDS);
        testSubscriber1.assertReceivedOnNext(Collections.singletonList(subPath));

        testSubscriber2.awaitValueCount(1, 10, TimeUnit.SECONDS);
        testSubscriber2.assertReceivedOnNext(Collections.singletonList(subPath));
    }

    @Test
    public void stopsTaskAfterTheThreadIsInterrupted() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(new NotifySubscribers(watchService, new CopyOnWriteArrayList<>()));
        executorService.shutdownNow();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        Assertions.assertThat(future.isDone()).isEqualTo(true);
    }

    @Test
    public void callsOnCompleteAfterTheThreadIsInterrupted() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();
        CopyOnWriteArrayList<Subscriber<? super Path>> subscribers = new CopyOnWriteArrayList<>();
        subscribers.add(testSubscriber);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new NotifySubscribers(watchService, subscribers));
        executorService.shutdownNow();

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        testSubscriber.assertCompleted();
    }
}
