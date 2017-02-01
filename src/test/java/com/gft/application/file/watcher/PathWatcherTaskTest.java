package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.path.CannotCreateObservable;
import com.gft.path.PathTreeNodeObservableFactory;
import com.gft.path.treenode.PathTreeNode;
import com.gft.path.watcher.CouldNotRegisterPath;
import com.gft.path.watcher.PathWatcher;
import com.gft.path.watcher.PathWatcherFactory;
import com.gft.path.watcher.async.AsyncPathWatcherFactory;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observable;

import java.nio.file.Path;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class PathWatcherTaskTest {

    @Test
    public void sendsPathsThroughWebSockets() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathTreeNodeObservableFactory pathTreeNodeObservableFactory = mock(PathTreeNodeObservableFactory.class);
        PathWatcherFactory pathWatcherFactory = mock(AsyncPathWatcherFactory.class);
        PathViewFactory pathViewFactory = new PathViewFactory();
        Path path = mock(Path.class);
        PathWatcher pathWatcher = mock(PathWatcher.class);
        Path pathFromPathWatcher = mock(Path.class);

        when(pathWatcherFactory.create()).thenReturn(pathWatcher);
        PathTreeNode pathTreeNode = new PathTreeNode(path);
        when(pathTreeNodeObservableFactory.createObservableForPath(path)).thenReturn(Observable.just(pathTreeNode));
        PathTreeNode pathTreeNodeFromPathWatcher = new PathTreeNode(pathFromPathWatcher);
        when(pathWatcher.iterator()).thenReturn(Collections.singleton(pathTreeNodeFromPathWatcher).iterator());

        PathWatcherTask pathWatcherService = new PathWatcherTask(
            path,
            simpMessagingTemplate,
            pathTreeNodeObservableFactory,
            pathViewFactory,
            pathWatcherFactory
        );

        pathWatcherService.run();

        verify(pathWatcher, times(1)).start(path);
        verify(pathWatcher, times(1)).registerPath(path);
        verify(pathWatcher, times(1)).registerPath(pathFromPathWatcher);
        verify(simpMessagingTemplate, times(1))
            .convertAndSend("/topic/new-path", pathViewFactory.createFromPathTreeNode(pathTreeNode));
        verify(simpMessagingTemplate, times(1))
            .convertAndSend("/topic/new-path", pathViewFactory.createFromPathTreeNode(pathTreeNodeFromPathWatcher));
    }

    @Test(expected = PathWatcherTaskFailed.class)
    public void throwsExceptionWhenCannotRegisterPath() throws Exception {
        PathTreeNodeObservableFactory pathTreeNodeObservableFactory = mock(PathTreeNodeObservableFactory.class);
        PathWatcherFactory pathWatcherFactory = mock(AsyncPathWatcherFactory.class);
        Path path = mock(Path.class);
        PathWatcher pathWatcher = mock(PathWatcher.class);

        when(pathWatcherFactory.create()).thenReturn(pathWatcher);
        PathTreeNode pathTreeNode = new PathTreeNode(path);
        when(pathTreeNodeObservableFactory.createObservableForPath(path)).thenReturn(Observable.just(pathTreeNode));
        doThrow(CouldNotRegisterPath.class).when(pathWatcher).registerPath(path);

        PathWatcherTask pathWatcherService = new PathWatcherTask(
            path,
            mock(SimpMessagingTemplate.class),
            pathTreeNodeObservableFactory,
            new PathViewFactory(),
            pathWatcherFactory
        );

        pathWatcherService.run();
    }

    @Test(expected = PathWatcherTaskFailed.class)
    public void throwsExceptionWhenCannotCreateObservableForPath() throws Exception {
        PathTreeNodeObservableFactory pathTreeNodeObservableFactory = mock(PathTreeNodeObservableFactory.class);
        PathWatcherFactory pathWatcherFactory = mock(AsyncPathWatcherFactory.class);
        Path path = mock(Path.class);

        when(pathWatcherFactory.create()).thenReturn(mock(PathWatcher.class));
        doThrow(CannotCreateObservable.class).when(pathTreeNodeObservableFactory).createObservableForPath(path);

        PathWatcherTask pathWatcherService = new PathWatcherTask(
            path,
            mock(SimpMessagingTemplate.class),
            pathTreeNodeObservableFactory,
            new PathViewFactory(),
            pathWatcherFactory
        );

        pathWatcherService.run();
    }
}
