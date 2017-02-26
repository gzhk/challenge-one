package com.gft.application.file.watcher;

import com.gft.node.NodePayloadObservableFactory;
import com.gft.watchservice.PathNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observer;

import java.nio.file.Path;

@Service
public class PathWatcherService {

    private final NodePayloadObservableFactory nodePayloadObservableFactory;

    @Autowired
    public PathWatcherService(
        @NotNull final NodePayloadObservableFactory nodePayloadObservableFactory
    ) {
        this.nodePayloadObservableFactory = nodePayloadObservableFactory;
    }

    public void watch(@NotNull final PathNode pathNode, @NotNull final Observer<Path> pathObserver) {
    }
}
