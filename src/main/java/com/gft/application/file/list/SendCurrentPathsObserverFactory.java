package com.gft.application.file.list;

import com.gft.application.file.model.PathViewFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

public final class SendCurrentPathsObserverFactory {

    private final PathViewFactory pathViewFactory;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Logger logger;

    public SendCurrentPathsObserverFactory(
        @NotNull final PathViewFactory pathViewFactory,
        @NotNull final SimpMessagingTemplate simpMessagingTemplate,
        @NotNull final Logger logger
    ) {
        this.pathViewFactory = pathViewFactory;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.logger = logger;
    }

    @NotNull
    public SendCurrentPathsObserver create(@NotNull final UUID uuid) {
        return new SendCurrentPathsObserver(uuid, pathViewFactory, simpMessagingTemplate, logger);
    }
}
