package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.path.PathTreeNodeObservableFactory;
import com.gft.path.watcher.PathWatcherFactory;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PathWatcherServiceTest {

    @Test
    public void startsNewTaskForPath() throws Exception {
        ExecutorService executorService = mock(ExecutorService.class);

        PathWatcherService pathWatcherService = new PathWatcherService(
            executorService,
            mock(SimpMessagingTemplate.class),
            mock(PathTreeNodeObservableFactory.class),
            new PathViewFactory(),
            mock(PathWatcherFactory.class)
        );

        pathWatcherService.watchPath(mock(Path.class));

        verify(executorService, times(1)).submit(isA(PathWatcherTask.class));
    }
}
