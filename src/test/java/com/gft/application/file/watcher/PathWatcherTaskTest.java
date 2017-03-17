package com.gft.application.file.watcher;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import rx.observers.TestSubscriber;

import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PathWatcherTaskTest {

    @Test
    public void notifiesObserverAboutNewPathsFromRootDir() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        TestSubscriber<Path> pathObserver = new TestSubscriber<>();
        PathWatcherTask pathWatcherTask = new PathWatcherTask(fileSystem);

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        pathWatcherTask.watch(rootPath, pathObserver);

        Path rootSubDir = rootPath.resolve("rootSubDir");
        Files.createDirectory(rootSubDir);

        Path rootSubFile = rootPath.resolve("rootSubFile.txt");
        Files.write(rootSubFile, Collections.emptyList());

        pathObserver.awaitValueCount(2, 10, TimeUnit.SECONDS);
        Assertions.assertThat(pathObserver.getOnNextEvents()).containsOnly(rootSubDir, rootSubFile);
    }

    @Test
    public void notifiesObserverAboutNewPathsFromRootSubDir() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        TestSubscriber<Path> pathObserver = new TestSubscriber<>();
        PathWatcherTask pathWatcherTask = new PathWatcherTask(fileSystem);


        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        Path subPath = rootPath.resolve("subPath");
        Files.createDirectory(subPath);

        pathWatcherTask.watch(rootPath, pathObserver);

        Path subPathDir = subPath.resolve("subPathDir");
        Files.createDirectory(subPathDir);

        Path subPathFile = subPath.resolve("subPathFile.txt");
        Files.write(subPathFile, Collections.emptyList());

        pathObserver.awaitValueCount(2, 10, TimeUnit.SECONDS);
        Assertions.assertThat(pathObserver.getOnNextEvents()).containsOnly(subPathDir, subPathFile);
    }

    @Test
    public void notifiesObserverAboutPathsThatWereCreatedUnderEmittedPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        TestSubscriber<Path> pathObserver = new TestSubscriber<>();
        PathWatcherTask pathWatcherTask = new PathWatcherTask(fileSystem);

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        pathWatcherTask.watch(rootPath, pathObserver);

        Path subPathDir = rootPath.resolve("subPathDir");
        Files.createDirectory(subPathDir);

        Path pathFromEmittedPath = subPathDir.resolve("fromEmittedPaths");
        Files.createDirectory(pathFromEmittedPath);

        pathObserver.awaitValueCount(2, 10, TimeUnit.SECONDS);
        Assertions.assertThat(pathObserver.getOnNextEvents()).containsOnly(subPathDir, pathFromEmittedPath);
    }

    @Test
    public void notifyObserversAboutMultiLevelPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        TestSubscriber<Path> pathObserver = new TestSubscriber<>();
        PathWatcherTask pathWatcherTask = new PathWatcherTask(fileSystem);

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        pathWatcherTask.watch(rootPath, pathObserver);

        Path multiLevelPath = rootPath.resolve("a/b/c/d/e");
        Files.createDirectories(multiLevelPath);

        Path multiLevelFile = rootPath.resolve("a/b/c/d/e/file.txt");
        Files.write(multiLevelFile, Collections.emptyList());

        pathObserver.awaitValueCount(6, 20, TimeUnit.SECONDS);

        Assertions
            .assertThat(pathObserver.getOnNextEvents())
            .containsOnly(
                rootPath.resolve("a"),
                rootPath.resolve("a/b"),
                rootPath.resolve("a/b/c"),
                rootPath.resolve("a/b/c/d"),
                rootPath.resolve("a/b/c/d/e"),
                rootPath.resolve("a/b/c/d/e/file.txt")
            );
    }

    @Test(timeout = 5000)
    public void closesExecutorService() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        TestSubscriber<Path> pathObserver = new TestSubscriber<>();
        PathWatcherTask pathWatcherTask = new PathWatcherTask(fileSystem);

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        pathWatcherTask.watch(rootPath, pathObserver);
        pathWatcherTask.close();

        pathObserver.awaitTerminalEvent();
        pathObserver.assertCompleted();
    }

    @Test(expected = ClosedWatchServiceException.class, timeout = 10000)
    public void closesWatchService() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        FileSystem fileSystemMock = Mockito.mock(FileSystem.class);
        Mockito.when(fileSystemMock.newWatchService()).thenReturn(watchService);

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        PathWatcherTask pathWatcherTask = new PathWatcherTask(fileSystemMock);
        pathWatcherTask.watch(rootPath, new TestSubscriber<>());
        pathWatcherTask.close();

        watchService.take();
    }
}
