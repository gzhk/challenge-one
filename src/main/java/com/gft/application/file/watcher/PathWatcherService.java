package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.path.PathTreeNodeObservableFactory;
import com.gft.path.watcher.PathWatcherFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

public class PathWatcherService {

    private final ExecutorService executorService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PathTreeNodeObservableFactory pathTreeNodeObservableFactory;
    private final PathViewFactory pathViewFactory;
    private final PathWatcherFactory pathWatcherFactory;

    public PathWatcherService(
        @NotNull final ExecutorService executorService,
        @NotNull final SimpMessagingTemplate simpMessagingTemplate,
        @NotNull final PathTreeNodeObservableFactory pathTreeNodeObservableFactory,
        @NotNull final PathViewFactory pathViewFactory,
        @NotNull final PathWatcherFactory pathWatcherFactory
    ) {
        this.executorService = executorService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.pathTreeNodeObservableFactory = pathTreeNodeObservableFactory;
        this.pathViewFactory = pathViewFactory;
        this.pathWatcherFactory = pathWatcherFactory;
    }

    public void watchPath(Path path) {
        executorService.submit(
            new PathWatcherTask(
                path,
                simpMessagingTemplate,
                pathTreeNodeObservableFactory,
                pathViewFactory,
                pathWatcherFactory
            )
        );
    }
}
