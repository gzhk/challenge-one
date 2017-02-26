package com.gft.watchservice;

import com.gft.watchservice.iterator.WatchServiceIterator;
import com.google.common.jimfs.Jimfs;
import edu.emory.mathcs.backport.java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.nio.file.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public final class OnSubscribeEmitNewPathsTest {

    @Test
    public void emitsPathsFromIteratorCreatedByFactory() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService fsWatchService = fileSystem.newWatchService();
        Path rootPath = fileSystem.getPath("/root");
        Path subPath = rootPath.resolve("tmp");

        Files.createDirectory(rootPath);
        Files.createDirectory(subPath);

        final List<Path> paths = Arrays.asList(rootPath, subPath);
        final Iterator<Path> iterator = paths.iterator();

        OnSubscribeEmitNewPaths onSubscribe = new OnSubscribeEmitNewPaths(
            rootPath,
            () -> fsWatchService,
            watchService -> new WatchServiceIterator() {
                @Override
                public void close() throws Exception {
                }

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Path next() {
                    return iterator.next();
                }
            }
        );

        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();
        onSubscribe.call(testSubscriber);

        testSubscriber.assertReceivedOnNext(paths);
        testSubscriber.assertCompleted();
    }

    @Test
    public void finishesWorkIfSubscriberIsUnSubscribed() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService fsWatchService = fileSystem.newWatchService();
        Path rootPath = fileSystem.getPath("/root");
        Path subPath = rootPath.resolve("tmp");

        Files.createDirectory(rootPath);
        Files.createDirectory(subPath);

        final List<Path> paths = Arrays.asList(rootPath, subPath);
        final Iterator<Path> iterator = paths.iterator();

        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();

        OnSubscribeEmitNewPaths onSubscribe = new OnSubscribeEmitNewPaths(
            rootPath,
            () -> fsWatchService,
            watchService -> new WatchServiceIterator() {
                @Override
                public void close() throws Exception {
                }

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Path next() {
                    Path next = iterator.next();
                    // Second element should not be emitted to the subscriber
                    testSubscriber.unsubscribe();

                    return next;
                }
            }
        );

        onSubscribe.call(testSubscriber);

        testSubscriber.assertReceivedOnNext(Collections.singletonList(rootPath));
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void doesNotReportsExceptionIfSubscriberIsUnSubscribed() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService fsWatchService = fileSystem.newWatchService();
        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        OnSubscribeEmitNewPaths onSubscribe = new OnSubscribeEmitNewPaths(
            rootPath,
            () -> fsWatchService,
            watchService -> new WatchServiceIterator() {
                @Override
                public void close() throws Exception {
                    throw new Exception("irrelevant exception");
                }

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Path next() {
                    return null;
                }
            }
        );

        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();
        testSubscriber.unsubscribe();
        onSubscribe.call(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void reportsExceptionToTheSubscriber() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService fsWatchService = fileSystem.newWatchService();
        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        OnSubscribeEmitNewPaths onSubscribe = new OnSubscribeEmitNewPaths(
            rootPath,
            () -> fsWatchService,
            watchService -> new WatchServiceIterator() {
                @Override
                public void close() throws Exception {
                }

                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public Path next() {
                    throw new NoSuchElementException();
                }
            }
        );

        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();
        onSubscribe.call(testSubscriber);

        testSubscriber.assertError(NoSuchElementException.class);
        testSubscriber.assertNotCompleted();
    }

    @Test(timeout = 10_000)
    public void registersRootPathInWatchService() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService fsWatchService = fileSystem.newWatchService();

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        OnSubscribeEmitNewPaths onSubscribe = new OnSubscribeEmitNewPaths(
            rootPath,
            () -> fsWatchService,
            watchService -> new WatchServiceIterator() {
                @Override
                public void close() throws Exception {
                }

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Path next() {
                    throw new NoSuchElementException();
                }
            }
        );

        onSubscribe.call(new TestSubscriber<>());

        Path newRootPath = rootPath.resolve("newRootPath");
        Files.createDirectory(newRootPath);

        WatchKey watchKey = fsWatchService.take();
        List<Path> collect = watchKey
            .pollEvents()
            .stream()
            .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
            .map(p -> ((Path) watchKey.watchable()).resolve(p))
            .collect(Collectors.toList());

        Assertions.assertThat(collect).containsOnly(newRootPath);
    }

    @Test(timeout = 10_000)
    public void registersRootPathRecursivelyInWatchService() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService fsWatchService = fileSystem.newWatchService();

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        Path subPath = rootPath.resolve("subDir");
        Files.createDirectory(subPath);

        OnSubscribeEmitNewPaths onSubscribe = new OnSubscribeEmitNewPaths(
            rootPath,
            () -> fsWatchService,
            watchService -> new WatchServiceIterator() {
                @Override
                public void close() throws Exception {
                }

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Path next() {
                    throw new NoSuchElementException();
                }
            }
        );

        onSubscribe.call(new TestSubscriber<>());

        Path newSubPath = subPath.resolve("newSubPath");
        Files.createDirectory(newSubPath);

        WatchKey watchKey = fsWatchService.take();
        List<Path> collect = watchKey
            .pollEvents()
            .stream()
            .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
            .map(p -> ((Path) watchKey.watchable()).resolve(p))
            .collect(Collectors.toList());

        Assertions.assertThat(collect).containsOnly(newSubPath);
    }

    @Test(expected = OnSubscribeEmitNewPathsException.class)
    public void throwsExceptionWhenCannotWalkRootPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService fsWatchService = fileSystem.newWatchService();
        Path rootPath = fileSystem.getPath("/root");

        OnSubscribeEmitNewPaths onSubscribe = new OnSubscribeEmitNewPaths(
            rootPath,
            () -> fsWatchService,
            watchService -> new WatchServiceIterator() {
                @Override
                public void close() throws Exception {
                }

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Path next() {
                    throw new NoSuchElementException();
                }
            }
        );

        onSubscribe.call(new TestSubscriber<>());
    }

    @Test(expected = OnSubscribeEmitNewPathsException.class)
    public void throwsExceptionWhenCannotRegisterPaths() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem();
        WatchService fsWatchService = fileSystem.newWatchService();
        Path rootPath = fileSystem.getPath("/root");

        OnSubscribeEmitNewPaths onSubscribe = new OnSubscribeEmitNewPaths(
            rootPath,
            () -> fsWatchService,
            watchService -> new WatchServiceIterator() {
                @Override
                public void close() throws Exception {
                }

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Path next() {
                    throw new NoSuchElementException();
                }
            }
        );

        fsWatchService.close();
        onSubscribe.call(new TestSubscriber<>());
    }
}
