package com.gft.path.watcher.async;

import com.gft.path.watcher.CouldNotCreatePathWatcher;
import com.gft.path.watcher.PathWatcher;
import com.gft.path.watcher.PathWatcherFactory;
import com.gft.path.watcher.async.AsyncPathWatcherFactory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
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

        PathWatcherFactory pathWatcherFactory = new AsyncPathWatcherFactory(fileSystem);
        PathWatcher pathWatcher = pathWatcherFactory.create();

        assertThat(pathWatcher, is(not(pathWatcherFactory.create())));
    }

    @Test(expected = CouldNotCreatePathWatcher.class)
    public void wrapsIOException() throws Exception {
        FileSystem fileSystem = mock(FileSystem.class);
        doThrow(IOException.class).when(fileSystem).newWatchService();
        PathWatcherFactory pathWatcherFactory = new AsyncPathWatcherFactory(fileSystem);

        pathWatcherFactory.create();
    }
}
