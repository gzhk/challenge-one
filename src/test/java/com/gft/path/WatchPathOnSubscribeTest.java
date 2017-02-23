package com.gft.path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import rx.Subscriber;

import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public final class WatchPathOnSubscribeTest {

    @Test(timeout = 20000)
    public void watchesRootPathRecursiveForNewPathsAndEmitsThemToTheSubscriber() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootDir = fileSystem.getPath("/root");
        Files.createDirectory(rootDir);

        Path subDir = rootDir.resolve("existing");
        Files.createDirectory(subDir);

        ConcurrentLinkedQueue<Path> emittedPaths = new ConcurrentLinkedQueue<>();

        Executors.newSingleThreadExecutor().submit(() -> {
            WatchPathOnSubscribe watchPathOnSubscribe = new WatchPathOnSubscribe(rootDir, watchService);
            watchPathOnSubscribe.call(new Subscriber<Path>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(Path path) {
                    emittedPaths.add(path);
                }
            });
        });

        // Wait until root path registers it self in WatchService
        Thread.sleep(500);

        Path rootSubDir = rootDir.resolve("rootSubDir");
        Files.createDirectory(rootSubDir);

        Path level2SubDir = rootSubDir.resolve("level2SubDir/somethingelse");
        Files.createDirectories(level2SubDir);

        Path rootFile = rootDir.resolve("file.txt");
        Files.write(rootFile, Collections.singleton("asd"), Charset.forName("UTF-8"));

        Path subFile = subDir.resolve("file.txt");
        Files.write(subFile, Collections.singleton("asd"), Charset.forName("UTF-8"));

        while (emittedPaths.size() < 5) {
        }

        fileSystem.close();

        Assertions
            .assertThat(emittedPaths)
            .containsOnly(
                rootSubDir,
                level2SubDir,
                rootSubDir.resolve("level2SubDir"),
                rootFile,
                subFile
            );
    }

    @Test(timeout = 5000)
    public void itDoesNotWatchForChangesWhenSubscriberIsUnSubscribed() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootDir = fileSystem.getPath("/root");
        Files.createDirectory(rootDir);

        WatchPathOnSubscribe watchPathOnSubscribe = new WatchPathOnSubscribe(rootDir, watchService);

        Subscriber<Path> subscriber = new Subscriber<Path>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Path path) {
            }
        };
        subscriber.unsubscribe();
        watchPathOnSubscribe.call(subscriber);

        fileSystem.close();

        // If we got here, that means WatchService didn't block us, because subscriber is unSubscribed
        Assert.assertTrue(true);
    }

    @Test(timeout = 5000)
    public void callsOnErrorMethod() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootDir = fileSystem.getPath("/root");
        Files.createDirectory(rootDir);

        ArrayBlockingQueue<Throwable> throwables = new ArrayBlockingQueue<>(1);

        Executors.newSingleThreadExecutor().submit(() -> {
            WatchPathOnSubscribe watchPathOnSubscribe = new WatchPathOnSubscribe(rootDir, watchService);
            watchPathOnSubscribe.call(new Subscriber<Path>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    throwables.add(e);
                }

                @Override
                public void onNext(Path path) {
                }
            });
        });

        fileSystem.close();

        Assertions.assertThat(throwables.take()).isInstanceOfAny(ClosedFileSystemException.class);
    }

    @Test
    public void reportsExceptionWhenCannotRegisterPathForWatchingChanges() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootDir = fileSystem.getPath("/root");

        List<Throwable> throwables = new ArrayList<>();

        WatchPathOnSubscribe watchPathOnSubscribe = new WatchPathOnSubscribe(rootDir, watchService);
        watchPathOnSubscribe.call(new Subscriber<Path>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                throwables.add(e);
            }

            @Override
            public void onNext(Path path) {
            }
        });

        Assertions.assertThat(throwables).hasSize(1);
        Assertions.assertThat(throwables.get(0)).isInstanceOf(WatchPathOnSubscribeException.class);
    }

    @Test
    public void doesNotReportExceptionIfSubscriberIsUnSubscribed() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootDir = fileSystem.getPath("/root");

        List<Throwable> throwables = new ArrayList<>();

        WatchPathOnSubscribe watchPathOnSubscribe = new WatchPathOnSubscribe(rootDir, watchService);
        Subscriber<Path> subscriber = new Subscriber<Path>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                throwables.add(e);
            }

            @Override
            public void onNext(Path path) {
            }
        };
        subscriber.unsubscribe();

        watchPathOnSubscribe.call(subscriber);

        Assertions.assertThat(throwables).hasSize(0);
    }

    @Test
    public void doesNotEmitsPathsIfSubscriberIsUnSubscribed() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootDir = fileSystem.getPath("/root");
        Files.createDirectory(rootDir);

        ConcurrentLinkedQueue<Path> emittedPaths = new ConcurrentLinkedQueue<>();

        Subscriber<Path> subscriber = new Subscriber<Path>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Path path) {
                emittedPaths.add(path);
            }
        };

        Executors.newSingleThreadExecutor().submit(() -> {
            WatchPathOnSubscribe watchPathOnSubscribe = new WatchPathOnSubscribe(rootDir, watchService);
            watchPathOnSubscribe.call(subscriber);
        });

        // Wait until root path registers it self in WatchService
        Thread.sleep(500);

        Path rootSubDir = rootDir.resolve("rootSubDir/shouldNotBeEmitted");
        Files.createDirectories(rootSubDir);

        while (emittedPaths.size() < 1) {
        }

        subscriber.unsubscribe();

        Assertions.assertThat(emittedPaths).containsOnly(rootDir.resolve("rootSubDir"));
    }

    @Test
    public void breaksLoopAndExecutesOn() throws Exception {

    }
}
