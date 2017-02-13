package com.gft.application.file.watcher;

import com.gft.node.NodePayloadObservableFactory;
import com.gft.path.PathNode;
import org.junit.Test;
import rx.Observable;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;

import static org.mockito.Mockito.*;

public class PathWatcherControllerTest {

    @Test
    public void sendsPathViewToTheWebSocket() throws Exception {
        NodePayloadObservableFactory observableFactory = mock(NodePayloadObservableFactory.class);
        Path path = mock(Path.class);
        ConnectableObservable<Path> connectableObservable = Observable.just(path).publish();
        when(observableFactory.createForNode(new PathNode(path))).thenReturn(connectableObservable);

        SendPathViewObserver sendPathViewObserver = mock(SendPathViewObserver.class);

        PathWatcherController pathWatcherController = new PathWatcherController(
            path,
            sendPathViewObserver,
            observableFactory
        );

        pathWatcherController.currentPaths();

        verify(sendPathViewObserver).onNext(path);
    }
}
