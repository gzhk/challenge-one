package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.node.NodePayloadObservableFactory;
import com.gft.path.PathNode;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observable;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;

import static org.mockito.Mockito.*;

public class PathWatcherControllerTest {

    @Test
    public void sendsPathViewToTheWebSocket() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();

        NodePayloadObservableFactory observableFactory = mock(NodePayloadObservableFactory.class);
        Path path = mock(Path.class);
        ConnectableObservable<Path> connectableObservable = Observable.just(path).publish();
        when(observableFactory.createForNode(new PathNode(path))).thenReturn(connectableObservable);

        PathWatcherController pathWatcherController = new PathWatcherController(
            path,
            simpMessagingTemplate,
            pathViewFactory,
            observableFactory
        );

        pathWatcherController.currentPaths();

        verify(simpMessagingTemplate, times(1)).convertAndSend(
            "/topic/new-path",
            pathViewFactory.createFrom(path)
        );
    }
}
