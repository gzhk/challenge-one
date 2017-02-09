package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.path.PathNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.file.Path;

public final class PathWatcherTask {

    private final PathWatcherService pathWatcherService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PathViewFactory pathViewFactory;

    public PathWatcherTask(
        @NotNull final PathWatcherService pathWatcherService,
        @NotNull final SimpMessagingTemplate simpMessagingTemplate,
        @NotNull final PathViewFactory pathViewFactory
    ) {
        this.pathWatcherService = pathWatcherService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.pathViewFactory = pathViewFactory;
    }

    public void watchAndSend(Path path) {
        pathWatcherService.watch(new PathNode(path), emittedPath -> {
            simpMessagingTemplate.convertAndSend("/topic/new-path", pathViewFactory.createFrom(emittedPath));
        });
    }
}
