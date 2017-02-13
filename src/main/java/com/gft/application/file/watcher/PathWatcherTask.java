package com.gft.application.file.watcher;

import com.gft.path.PathNode;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class PathWatcherTask {

    private final PathWatcherService pathWatcherService;
    private final SendPathViewObserver pathObserver;

    public PathWatcherTask(
        @NotNull final PathWatcherService pathWatcherService,
        @NotNull final SendPathViewObserver pathObserver
    ) {
        this.pathWatcherService = pathWatcherService;
        this.pathObserver = pathObserver;
    }

    public void watchAndSend(Path path) {
        pathWatcherService.watch(new PathNode(path), pathObserver);
    }
}
