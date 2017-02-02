package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.path.PathTreeNodeObservableFactory;
import com.gft.path.watcher.CouldNotRegisterPath;
import com.gft.path.watcher.PathWatcher;
import com.gft.path.watcher.PathWatcherFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observable;

import java.nio.file.Path;

public final class PathWatcherTask implements Runnable {

    private final Path path;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PathTreeNodeObservableFactory pathTreeNodeObservableFactory;
    private final PathViewFactory pathViewFactory;
    private final PathWatcherFactory pathWatcherFactory;

    public PathWatcherTask(
        @NotNull final Path path,
        @NotNull final SimpMessagingTemplate simpMessagingTemplate,
        @NotNull final PathTreeNodeObservableFactory pathTreeNodeObservableFactory,
        @NotNull final PathViewFactory pathViewFactory,
        @NotNull final PathWatcherFactory pathWatcherFactory
    ) {
        this.path = path;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.pathTreeNodeObservableFactory = pathTreeNodeObservableFactory;
        this.pathViewFactory = pathViewFactory;
        this.pathWatcherFactory = pathWatcherFactory;
    }

    @Override
    public void run() {
        try (PathWatcher pathWatcher = pathWatcherFactory.create()) {
            pathWatcher.start();

            Observable
                .merge(pathTreeNodeObservableFactory.createObservableForPath(path), Observable.from(pathWatcher))
                .subscribe(pathTreeNode -> {
                    try {
                        pathWatcher.registerPath(pathTreeNode.getPath());
                    } catch (CouldNotRegisterPath e) {
                        throw new PathWatcherTaskFailed("Could not register path.", e);
                    }

                    simpMessagingTemplate.convertAndSend(
                        "/topic/new-path",
                        pathViewFactory.createFromPathTreeNode(pathTreeNode)
                    );
                });
        } catch (Exception e) {
            throw new PathWatcherTaskFailed("Could not create path watcher.", e);
        }
    }
}
