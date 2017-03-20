package com.gft.application.file.list;

import com.gft.application.file.model.PathViewFactory;
import com.gft.node.NodePayloadObservableFactory;
import com.gft.node.PathNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import rx.Observable;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class PathWatcherControllerTest {

    @Test
    public void sendsPathViewToTheWebSocket() throws Exception {
        NodePayloadObservableFactory observableFactory = mock(NodePayloadObservableFactory.class);
        Path path = mock(Path.class);
        ConnectableObservable<Path> connectableObservable = Observable.just(path).publish();
        when(observableFactory.create(new PathNode(path))).thenReturn(connectableObservable);

        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        SendCurrentPathsObserverFactory factory = new SendCurrentPathsObserverFactory(
            new PathViewFactory(),
            simpMessagingTemplate,
            mock(Logger.class)
        );

        UUID clientUUID = UUID.randomUUID();

        PathWatcherController pathWatcherController = new PathWatcherController(path, factory, observableFactory);
        SimpMessageHeaderAccessor simpMessageHeaderAccessor = SimpMessageHeaderAccessor.wrap(new GenericMessage<>("irrelevant", new HashMap<>()));
        simpMessageHeaderAccessor.setNativeHeader("token", clientUUID.toString());
        pathWatcherController.currentPaths(simpMessageHeaderAccessor);

        Map<String, Object> headers = new HashMap<>();
        headers.put("token", clientUUID);

        verify(simpMessagingTemplate, times(1)).convertAndSend("/topic/current-paths", new PathViewFactory().createFrom(path), headers);
    }
}
