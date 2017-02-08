package com.gft.path.watcher.async;

import com.gft.node.watcher.CouldNotCreatePayloadWatcher;
import com.gft.node.watcher.PayloadWatcher;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.WatchService;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AsyncPathWatcherFactoryTest {

    @Test
    public void createsNewInstanceOfPathWatcher() throws Exception {
        FileSystem fileSystem = mock(FileSystem.class);
        when(fileSystem.newWatchService()).thenReturn(mock(WatchService.class));

        AsyncPathWatcherFactory pathWatcherFactory = new AsyncPathWatcherFactory(fileSystem);
        PayloadWatcher<Path> pathWatcher = pathWatcherFactory.create();

        assertThat(pathWatcher, is(not(pathWatcherFactory.create())));
    }

    @Test(expected = CouldNotCreatePayloadWatcher.class)
    public void wrapsIOException() throws Exception {
        FileSystem fileSystem = mock(FileSystem.class);
        doThrow(IOException.class).when(fileSystem).newWatchService();
        AsyncPathWatcherFactory pathWatcherFactory = new AsyncPathWatcherFactory(fileSystem);

        pathWatcherFactory.create();
    }
}
