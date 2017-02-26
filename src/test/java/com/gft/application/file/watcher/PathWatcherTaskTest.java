package com.gft.application.file.watcher;

import com.gft.watchservice.PathNode;
import org.junit.Test;

import java.nio.file.Path;

import static org.mockito.Mockito.*;

public class PathWatcherTaskTest {

    @Test
    public void sendsThroughWebSocketsEmittedElements() throws Exception {
        PathWatcherService pathWatcherService = mock(PathWatcherService.class);
        SendPathViewObserver pathObserver = mock(SendPathViewObserver.class);

        PathWatcherTask task = new PathWatcherTask(pathWatcherService, pathObserver);

        Path path = mock(Path.class);
        task.watchAndSend(path);

        verify(pathWatcherService, times(1)).watch(new PathNode(path), pathObserver);
    }
}
