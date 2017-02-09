package com.gft.application.file.watcher;

import com.gft.node.NodePayloadObservableFactory;
import com.gft.path.PathNode;
import com.gft.path.watcher.async.AsyncPathWatcher;
import com.gft.path.watcher.async.AsyncPathWatcherFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;

@Service
public class PathWatcherService {

    private final AsyncPathWatcherFactory asyncPathWatcherFactory;
    private final NodePayloadObservableFactory nodePayloadObservableFactory;

    @Autowired
    public PathWatcherService(
        @NotNull final AsyncPathWatcherFactory asyncPathWatcherFactory,
        @NotNull final NodePayloadObservableFactory nodePayloadObservableFactory
    ) {
        this.asyncPathWatcherFactory = asyncPathWatcherFactory;
        this.nodePayloadObservableFactory = nodePayloadObservableFactory;
    }

    public void watch(@NotNull final PathNode pathNode, @NotNull final Action1<Path> subscriber) {
//        AsyncPathWatcher asyncPathWatcher = asyncPathWatcherFactory.create();
//        ConnectableObservable<Path> connectableObservable = nodePayloadObservableFactory.createWithWatcher(pathNode, asyncPathWatcher);
//        connectableObservable.subscribe(subscriber);
//        connectableObservable.connect();
    }
}
