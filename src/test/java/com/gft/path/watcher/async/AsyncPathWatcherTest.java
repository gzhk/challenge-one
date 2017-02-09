package com.gft.path.watcher.async;

import com.gft.collections.BlockingQueueIterator;
import com.gft.node.watcher.CouldNotRegisterPayload;
import com.gft.node.watcher.PayloadWatcher;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AsyncPathWatcherTest {

    @Test
    public void registersPathWithWatchService() throws Exception {
        WatchService watchService = mock(WatchService.class);
        PayloadWatcher<Path> pathWatcher = new AsyncPathWatcher(watchService, new ConcurrentHashMap<>(), new LinkedBlockingQueue<>());

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(true);
        when(path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenReturn(mock(WatchKey.class));

        pathWatcher.call(path);

        verify(path).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test(expected = CouldNotRegisterPayload.class)
    public void wrapsIOException() throws Exception {
        WatchService watchService = mock(WatchService.class);
        PayloadWatcher<Path> pathWatcher = new AsyncPathWatcher(watchService, new ConcurrentHashMap<>(), new LinkedBlockingQueue<>());

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(true);

        when(path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenThrow(IOException.class);

        pathWatcher.call(path);
    }

    @Test
    public void itIsIterable() throws Exception {
        assertThat(
            new AsyncPathWatcher(mock(WatchService.class), new ConcurrentHashMap<>(), new LinkedBlockingQueue<>()),
            is(instanceOf(Iterable.class))
        );
    }

    @Test
    public void returnsNewPathsIterator() throws Exception {
        AsyncPathWatcher pathTreeNodes = new AsyncPathWatcher(mock(WatchService.class), new ConcurrentHashMap<>(), new LinkedBlockingQueue<>());
        assertThat(pathTreeNodes.iterator(), is(instanceOf(BlockingQueueIterator.class)));
    }

    @Test
    public void itRegistersOnlyDirectories() throws Exception {
        WatchService watchService = mock(WatchService.class);
        PayloadWatcher<Path> pathWatcher = new AsyncPathWatcher(watchService, new ConcurrentHashMap<>(), new LinkedBlockingQueue<>());

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(false);
        pathWatcher.call(path);

        verify(path, never()).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }
}
