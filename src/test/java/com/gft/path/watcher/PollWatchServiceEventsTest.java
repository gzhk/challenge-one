package com.gft.path.watcher;

import com.gft.path.treenode.PathTreeNode;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PollWatchServiceEventsTest {

    @Test
    public void itIsRunnable() throws Exception {
        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(
            mock(Path.class),
            mock(WatchService.class),
            new ArrayBlockingQueue<>(16)
        );

        assertThat(pollWatchServiceEvents, is(instanceOf(Runnable.class)));
    }

    @Test
    public void addsPathsRecursively() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fileSystem.getPath("C:\\root");
        Files.createDirectory(rootPath);

        Path directory = rootPath.resolve("directory");
        Files.createDirectory(directory);

        Path file = rootPath.resolve("file.txt");
        Files.write(file, ImmutableList.of("content"), StandardCharsets.UTF_8);

        Path subDirectory = directory.resolve("subdirectory");
        Files.createDirectory(subDirectory);

        ArrayBlockingQueue<PathTreeNode> pathQueue = new ArrayBlockingQueue<>(16);
        WatchService watchService = mock(WatchService.class);

        WatchKey watchKey = mock(WatchKey.class);
        when(watchService.take()).thenReturn(watchKey);

        ArrayList<WatchEvent<?>> watchEvents = new ArrayList<>();
        WatchEvent watchEvent = mock(WatchEvent.class);
        watchEvents.add(watchEvent);
        when(watchKey.pollEvents()).thenReturn(watchEvents);

        when(watchEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(watchEvent.context()).thenReturn(rootPath);

        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(rootPath, watchService, pathQueue);
        pollWatchServiceEvents.run();

        assertTrue(pathQueue.contains(new PathTreeNode(rootPath)));
        assertTrue(pathQueue.contains(new PathTreeNode(directory, new PathTreeNode(rootPath))));
        assertTrue(pathQueue.contains(new PathTreeNode(subDirectory, new PathTreeNode(directory))));
        assertTrue(pathQueue.contains(new PathTreeNode(file, new PathTreeNode(rootPath))));
    }

    @Test
    public void abortsExecutionWhenGotInterruptedException() throws Exception {
        ArrayBlockingQueue<PathTreeNode> pathQueue = new ArrayBlockingQueue<>(16);
        WatchService watchService = mock(WatchService.class);

        when(watchService.take()).thenThrow(InterruptedException.class);

        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(mock(Path.class), watchService, pathQueue);
        pollWatchServiceEvents.run();
    }

    @Test
    public void itFinishesIfWatchKeyIsNoLongerValid() throws Exception {
        ArrayBlockingQueue<PathTreeNode> pathQueue = new ArrayBlockingQueue<>(16);
        WatchService watchService = mock(WatchService.class);

        WatchKey watchKey = mock(WatchKey.class);
        WatchKey secondWatchKey = mock(WatchKey.class);
        when(watchService.take()).thenReturn(watchKey, secondWatchKey);

        ArrayList<WatchEvent<?>> watchEvents = new ArrayList<>();
        WatchEvent watchEvent = mock(WatchEvent.class);
        watchEvents.add(watchEvent);

        when(watchEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        Path path = mock(Path.class);
        when(watchEvent.context()).thenReturn(path);
        when(watchKey.isValid()).thenReturn(false);

        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(mock(Path.class), watchService, pathQueue);
        pollWatchServiceEvents.run();
        verify(secondWatchKey, never()).pollEvents();
    }

    @Test(expected = RuntimeException.class)
    public void wrapsIOExceptionInRuntimeException() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fileSystem.getPath("C:\\root");
        Files.createDirectory(rootPath);

        Path directory = rootPath.resolve("directory");
        Files.createDirectory(directory);

        BlockingQueue<PathTreeNode> pathQueue = mock(BlockingQueue.class);
        doThrow(InterruptedException.class).when(pathQueue).put(any());
        WatchService watchService = mock(WatchService.class);

        WatchKey watchKey = mock(WatchKey.class);
        when(watchService.take()).thenReturn(watchKey);

        WatchEvent watchEvent = mock(WatchEvent.class);
        when(watchKey.pollEvents()).thenReturn(Collections.singletonList(watchEvent));

        when(watchEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(watchEvent.context()).thenReturn(rootPath);

        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(rootPath, watchService, pathQueue);
        pollWatchServiceEvents.run();
    }
}
