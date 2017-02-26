package com.gft.watchservice;

import com.gft.watchservice.iterator.WatchServiceIteratorFactory;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.observables.ConnectableObservable;

import java.nio.file.Path;

public final class PathObservableFactory {

    private final WatchServiceFactory watchServiceFactory;
    private final WatchServiceIteratorFactory watchServiceIteratorFactory;

    public PathObservableFactory(
        @NotNull final WatchServiceFactory watchServiceFactory,
        @NotNull final WatchServiceIteratorFactory watchServiceIteratorFactory
    ) {
        this.watchServiceFactory = watchServiceFactory;
        this.watchServiceIteratorFactory = watchServiceIteratorFactory;
    }

    @NotNull
    public ConnectableObservable<Path> create(@NotNull final Path rootPath) {
        return Observable
            .create(new OnSubscribeEmitNewPaths(rootPath, watchServiceFactory, watchServiceIteratorFactory))
            .publish();
    }
}
