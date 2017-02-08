package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.node.NodePayloadObservableFactory;
import com.gft.path.PathNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;

@Controller
public final class PathWatcherController {

    private final Path dir;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PathViewFactory pathViewFactory;
    private final NodePayloadObservableFactory nodePayloadObservableFactory;

    @Autowired
    public PathWatcherController(
        @Value("${dir}") Path dir,
        @NotNull final SimpMessagingTemplate simpMessagingTemplate,
        @NotNull final PathViewFactory pathViewFactory,
        @NotNull final NodePayloadObservableFactory nodePayloadObservableFactory
    ) {
        this.dir = dir;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.pathViewFactory = pathViewFactory;
        this.nodePayloadObservableFactory = nodePayloadObservableFactory;
    }

    @MessageMapping("/current-paths")
    public void currentPaths() {
        ConnectableObservable<Path> connectableObservable = nodePayloadObservableFactory.createForNode(new PathNode(dir));

        connectableObservable
            .map(pathViewFactory::createFrom)
            .subscribe(pathView -> simpMessagingTemplate.convertAndSend("/topic/new-path", pathView));

        connectableObservable.connect();
    }
}
