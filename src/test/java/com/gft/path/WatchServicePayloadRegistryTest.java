package com.gft.path;

import com.gft.node.watcher.CouldNotRegisterPayload;
import com.gft.node.watcher.PayloadRegistry;
import org.junit.Test;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class WatchServicePayloadRegistryTest {

    @Test
    public void registersPathWithWatchService() throws Exception {
        WatchService watchService = mock(WatchService.class);
        ConcurrentHashMap<WatchKey, Path> keys = new ConcurrentHashMap<>();

        PayloadRegistry<Path> payloadRegistry = new WatchServicePayloadRegistry(
            mock(ExecutorService.class),
            watchService,
            new LinkedBlockingQueue<>(),
            keys
        );

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(true);
        WatchKey watchKey = mock(WatchKey.class);
        when(path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenReturn(watchKey);

        payloadRegistry.registerPayload(path);

        verify(path).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        assertThat(keys.get(watchKey), is(path));
    }

    @Test(expected = CouldNotRegisterPayload.class)
    public void wrapsIOException() throws Exception {
        WatchService watchService = mock(WatchService.class);

        PayloadRegistry<Path> payloadRegistry = new WatchServicePayloadRegistry(
            mock(ExecutorService.class),
            watchService,
            new LinkedBlockingQueue<>(),
            new ConcurrentHashMap<>()
        );

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(true);

        when(path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenThrow(IOException.class);

        payloadRegistry.registerPayload(path);
    }

    @Test
    public void itRegistersOnlyDirectories() throws Exception {
        WatchService watchService = mock(WatchService.class);

        PayloadRegistry<Path> payloadRegistry = new WatchServicePayloadRegistry(
            mock(ExecutorService.class),
            watchService,
            new LinkedBlockingQueue<>(),
            new ConcurrentHashMap<>()
        );

        Path path = mock(Path.class);
        FileSystem fileSystem = mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributes basicFileAttributes = mock(BasicFileAttributes.class);

        when(path.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(path, BasicFileAttributes.class)).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isDirectory()).thenReturn(false);
        payloadRegistry.registerPayload(path);

        verify(path, never()).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test(timeout = 5000)
    public void returnsObservableCreatedFromNewPathsItems() throws Exception {
        LinkedBlockingQueue<Path> newPaths = new LinkedBlockingQueue<>();

        PayloadRegistry<Path> payloadRegistry = new WatchServicePayloadRegistry(
            mock(ExecutorService.class),
            mock(WatchService.class),
            newPaths,
            new ConcurrentHashMap<>()
        );

        Path path = mock(Path.class);
        newPaths.add(path);

        Queue<Path> paths = new ConcurrentLinkedQueue<>();

        payloadRegistry.changes()
            .subscribeOn(Schedulers.newThread())
            .subscribe(paths::add);

        while (paths.size() < 1) {
            // wait for path to appear
        }

        assertThat(paths, hasItem(path));
    }

    @Test
    public void itStartsPollWatchServiceEventsTaskDuringInitialization() throws Exception {
        ExecutorService executorService = mock(ExecutorService.class);

        WatchServicePayloadRegistry payloadRegistry = new WatchServicePayloadRegistry(
            executorService,
            mock(WatchService.class),
            new LinkedBlockingQueue<>(),
            new ConcurrentHashMap<>()
        );

        payloadRegistry.startWatching();

        verify(executorService, times(1)).submit(isA(PollWatchServiceEvents.class));
    }
}
