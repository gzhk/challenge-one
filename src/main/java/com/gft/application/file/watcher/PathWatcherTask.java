package com.gft.application.file.watcher;

import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.file.Path;

public final class PathWatcherTask {

    private final PathWatcherService pathWatcherService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public PathWatcherTask(
        @NotNull final PathWatcherService pathWatcherService,
        @NotNull final SimpMessagingTemplate simpMessagingTemplate
    ) {
        this.pathWatcherService = pathWatcherService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void watchAndSend(Path path) {

    }
}
