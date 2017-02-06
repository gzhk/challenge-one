package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.path.PathTreeNodeObservableFactory;
import com.gft.path.treenode.PathTreeNode;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observable;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class PathWatcherControllerTest {

    @Test
    public void sendsPathViewToTheWebSocket() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathTreeNodeObservableFactory pathTreeNodeObservableFactory = mock(PathTreeNodeObservableFactory.class);
        PathViewFactory pathViewFactory = new PathViewFactory();

        PathWatcherController pathWatcherController = new PathWatcherController(
            "/tmp",
            simpMessagingTemplate,
            pathTreeNodeObservableFactory,
            pathViewFactory
        );

        Path path = Paths.get("/tmp");
        PathTreeNode pathTreeNode = new PathTreeNode(path);

        when(pathTreeNodeObservableFactory.createObservableForPath(path)).thenReturn(Observable.just(pathTreeNode));

        pathWatcherController.currentPaths();

        verify(simpMessagingTemplate).convertAndSend("/topic/new-path", pathViewFactory.createFromPathTreeNode(pathTreeNode));
    }
}
