package com.gft.application.file.watcher;

import com.gft.node.NodePayloadObservableFactory;
import com.gft.watchservice.PathNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;

@Controller
public final class PathWatcherController {

    private final Path dir;
    private final SendPathViewObserver sendPathViewObserver;
    private final NodePayloadObservableFactory nodePayloadObservableFactory;

    @Autowired
    public PathWatcherController(
        @Value("${dir}") final Path dir,
        @NotNull final SendPathViewObserver sendPathViewObserver,
        @NotNull final NodePayloadObservableFactory nodePayloadObservableFactory
    ) {
        this.dir = dir;
        this.sendPathViewObserver = sendPathViewObserver;
        this.nodePayloadObservableFactory = nodePayloadObservableFactory;
    }

    @MessageMapping("/current-paths")
    public void currentPaths() {
        ConnectableObservable<Path> connectableObservable = nodePayloadObservableFactory.create(new PathNode(dir));
        connectableObservable.subscribe(sendPathViewObserver);
        connectableObservable.connect();
    }
}
