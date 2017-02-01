package com.gft.path.watcher.async;

import com.gft.path.watcher.CouldNotRegisterPath;
import com.gft.path.watcher.NewPathsIterator;
import com.gft.path.watcher.PathWatcher;
import com.gft.path.watcher.PollWatchServiceEvents;
import com.gft.path.watcher.async.AsyncPathWatcher;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AsyncPathWatcherTest {

    @Test
    public void registersPathWithWatchService() throws Exception {
        WatchService watchService = mock(WatchService.class);
        PathWatcher pathWatcher = new AsyncPathWatcher(watchService, mock(ExecutorService.class));

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(true);
        pathWatcher.registerPath(path);

        verify(path).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test(expected = CouldNotRegisterPath.class)
    public void wrapsIOException() throws Exception {
        WatchService watchService = mock(WatchService.class);
        PathWatcher pathWatcher = new AsyncPathWatcher(watchService, mock(ExecutorService.class));

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(true);

        when(path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenThrow(IOException.class);

        pathWatcher.registerPath(path);
    }

    @Test
    public void itIsAutoClosable() throws Exception {
        assertThat(new AsyncPathWatcher(mock(WatchService.class), mock(ExecutorService.class)), is(instanceOf(AutoCloseable.class)));
    }

    @Test
    public void itClosesWatchServiceAndExecutorService() throws Exception {
        WatchService watchService = mock(WatchService.class);
        ExecutorService executorService = mock(ExecutorService.class);
        AsyncPathWatcher pathWatcher = new AsyncPathWatcher(watchService, executorService);

        pathWatcher.close();
        verify(watchService).close();
        verify(executorService).shutdown();
    }

    @Test
    public void itIsIterable() throws Exception {
        assertThat(new AsyncPathWatcher(mock(WatchService.class), mock(ExecutorService.class)), is(instanceOf(Iterable.class)));
    }

    @Test
    public void returnsNewPathsIterator() throws Exception {
        AsyncPathWatcher pathTreeNodes = new AsyncPathWatcher(mock(WatchService.class), mock(ExecutorService.class));
        assertThat(pathTreeNodes.iterator(), is(instanceOf(NewPathsIterator.class)));
    }

    @Test
    public void startsPollsWatchEventsDuringInitialization() throws Exception {
        ExecutorService executorService = mock(ExecutorService.class);
        PathWatcher pathTreeNodes = new AsyncPathWatcher(mock(WatchService.class), executorService);

        pathTreeNodes.start(mock(Path.class));

        ArgumentCaptor<PollWatchServiceEvents> argument = ArgumentCaptor.forClass(PollWatchServiceEvents.class);
        verify(executorService).submit(argument.capture());
    }

    @Test
    public void itRegistersOnlyDirectories() throws Exception {
        WatchService watchService = mock(WatchService.class);
        PathWatcher pathWatcher = new AsyncPathWatcher(watchService, mock(ExecutorService.class));

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(false);
        pathWatcher.registerPath(path);

        verify(path, never()).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }
}
