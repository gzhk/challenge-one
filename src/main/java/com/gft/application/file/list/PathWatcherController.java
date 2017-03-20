package com.gft.application.file.list;

import com.gft.node.NodePayloadObservableFactory;
import com.gft.node.PathNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;
import java.util.UUID;

@Controller
public final class PathWatcherController {

    private final Path dir;
    private final SendCurrentPathsObserverFactory sendCurrentPathsObserverFactory;
    private final NodePayloadObservableFactory nodePayloadObservableFactory;

    @Autowired
    public PathWatcherController(
        @Value("${dir}") final Path dir,
        @NotNull final SendCurrentPathsObserverFactory sendCurrentPathsObserverFactory,
        @NotNull final NodePayloadObservableFactory nodePayloadObservableFactory
    ) {
        this.dir = dir;
        this.sendCurrentPathsObserverFactory = sendCurrentPathsObserverFactory;
        this.nodePayloadObservableFactory = nodePayloadObservableFactory;
    }

    @MessageMapping("/current-paths")
    public void currentPaths(SimpMessageHeaderAccessor headerAccessor) {
        ConnectableObservable<Path> connectableObservable = nodePayloadObservableFactory.create(new PathNode(dir));
        connectableObservable.subscribe(sendCurrentPathsObserverFactory.create(UUID.fromString(headerAccessor.getNativeHeader("token").get(0))));
        connectableObservable.connect();
    }
}
