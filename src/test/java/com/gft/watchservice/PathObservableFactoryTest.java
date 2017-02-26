package com.gft.watchservice;

import com.gft.watchservice.iterator.RegistersPolledWatchServicePathsFactory;
import com.google.common.jimfs.Jimfs;
import edu.emory.mathcs.backport.java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;

import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class PathObservableFactoryTest {

    @Test
    public void createsConnectableObservableForGivenPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService watchService = fileSystem.newWatchService();

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        Path subPath = rootPath.resolve("subPath");
        Files.createDirectory(subPath);

        PathObservableFactory pathObservableFactory = new PathObservableFactory(() -> watchService, new RegistersPolledWatchServicePathsFactory());
        ConnectableObservable<Path> pathConnectableObservable = pathObservableFactory.create(rootPath);

        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();
        pathConnectableObservable.subscribe(testSubscriber);

        Executors.newSingleThreadExecutor().submit((Runnable) pathConnectableObservable::connect);

        // Wait for paths to register them with WatchService
        // todo Find better way..
        Thread.sleep(500);

        Path newPath = rootPath.resolve("newPath");
        Files.createDirectory(newPath);

        Path newPath2 = subPath.resolve("newPath2");
        Files.createDirectory(newPath2);

        Path newPath3 = subPath.resolve("newPath3");
        Files.createDirectory(newPath3);

        Path file = rootPath.resolve("file.txt");
        Files.write(file, Collections.emptyList(), Charset.forName("UTF-8"));

        testSubscriber.awaitValueCount(4, 10, TimeUnit.SECONDS);
        Assertions
            .assertThat(testSubscriber.getOnNextEvents())
            .containsOnly(newPath, newPath2, newPath3, file);
    }
}
