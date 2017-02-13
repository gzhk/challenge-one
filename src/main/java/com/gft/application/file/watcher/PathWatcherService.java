package com.gft.application.file.watcher;

import com.gft.node.NodePayloadObservableFactory;
import com.gft.path.PathNode;
import com.gft.path.WatchServicePayloadRegistry;
import com.gft.path.WatchServicePayloadRegistryFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import rx.Observer;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;

@Service
public class PathWatcherService {

    private final WatchServicePayloadRegistryFactory payloadRegistryFactory;
    private final NodePayloadObservableFactory nodePayloadObservableFactory;

    public PathWatcherService(
        @NotNull final WatchServicePayloadRegistryFactory payloadRegistryFactory,
        @NotNull final NodePayloadObservableFactory nodePayloadObservableFactory
    ) {
        this.payloadRegistryFactory = payloadRegistryFactory;
        this.nodePayloadObservableFactory = nodePayloadObservableFactory;
    }

    public void watch(@NotNull final PathNode pathNode, @NotNull final Observer<Path> pathObserver) {
        WatchServicePayloadRegistry payloadRegistry = payloadRegistryFactory.create();
        ConnectableObservable<Path> connectableObservable = nodePayloadObservableFactory.createWithWatcher(pathNode, payloadRegistry);
        connectableObservable.subscribe(pathObserver);
        payloadRegistry.startWatching();
        connectableObservable.connect();
    }
}
