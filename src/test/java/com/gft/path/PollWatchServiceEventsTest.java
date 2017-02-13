package com.gft.path;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PollWatchServiceEventsTest {

    @Test
    public void itIsRunnable() throws Exception {
        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(
            mock(WatchService.class),
            new ConcurrentHashMap<>(),
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

        ArrayBlockingQueue<Path> pathQueue = new ArrayBlockingQueue<>(16);
        WatchService watchService = mock(WatchService.class);

        WatchKey watchKey = mock(WatchKey.class);
        when(watchService.take()).thenReturn(watchKey);

        ArrayList<WatchEvent<?>> watchEvents = new ArrayList<>();
        WatchEvent watchEvent = mock(WatchEvent.class);
        watchEvents.add(watchEvent);
        when(watchKey.pollEvents()).thenReturn(watchEvents);

        when(watchEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(watchEvent.context()).thenReturn(rootPath);

        ConcurrentMap<WatchKey, Path> keys = new ConcurrentHashMap<>();
        keys.put(watchKey, rootPath);

        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(watchService, keys, pathQueue);
        pollWatchServiceEvents.run();

        assertThat(pathQueue, hasItems(rootPath, directory, subDirectory, file));
    }

    @Test
    public void abortsExecutionWhenGotInterruptedException() throws Exception {
        ArrayBlockingQueue<Path> pathQueue = new ArrayBlockingQueue<>(16);
        WatchService watchService = mock(WatchService.class);

        when(watchService.take()).thenThrow(InterruptedException.class);

        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(watchService, new ConcurrentHashMap<>(), pathQueue);
        pollWatchServiceEvents.run();
    }

    @Test
    public void itFinishesIfWatchKeyIsNoLongerValid() throws Exception {
        ArrayBlockingQueue<Path> pathQueue = new ArrayBlockingQueue<>(16);
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

        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(watchService, new ConcurrentHashMap<>(), pathQueue);
        pollWatchServiceEvents.run();
        verify(secondWatchKey, never()).pollEvents();
    }

    @Test(expected = RuntimeException.class)
    @SuppressWarnings("unchecked")
    public void wrapsIOExceptionInRuntimeException() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fileSystem.getPath("C:\\root");
        Files.createDirectory(rootPath);

        Path directory = rootPath.resolve("directory");
        Files.createDirectory(directory);

        BlockingQueue<Path> pathQueue = mock(BlockingQueue.class);
        doThrow(InterruptedException.class).when(pathQueue).put(any());
        WatchService watchService = mock(WatchService.class);

        WatchKey watchKey = mock(WatchKey.class);
        when(watchService.take()).thenReturn(watchKey);

        WatchEvent watchEvent = mock(WatchEvent.class);
        when(watchKey.pollEvents()).thenReturn(Collections.singletonList(watchEvent));

        when(watchEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(watchEvent.context()).thenReturn(rootPath);

        PollWatchServiceEvents pollWatchServiceEvents = new PollWatchServiceEvents(watchService, new ConcurrentHashMap<>(), pathQueue);
        pollWatchServiceEvents.run();
    }
}
