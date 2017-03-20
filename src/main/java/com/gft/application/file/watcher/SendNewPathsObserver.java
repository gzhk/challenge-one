package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observer;

import java.nio.file.Path;
import java.util.Arrays;

public class SendNewPathsObserver implements Observer<Path> {

    private final PathViewFactory pathViewFactory;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Logger logger;

    public SendNewPathsObserver(
        @NotNull final PathViewFactory pathViewFactory,
        @NotNull final SimpMessagingTemplate simpMessagingTemplate,
        @NotNull final Logger logger
    ) {
        this.pathViewFactory = pathViewFactory;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.logger = logger;
    }

    @Override
    public void onCompleted() {
        // do nothing
    }

    @Override
    public void onError(final Throwable e) {
        logger.error(e.getMessage());
        logger.error(Arrays.toString(e.getStackTrace()));
    }

    @Override
    public void onNext(final Path path) {
        simpMessagingTemplate.convertAndSend("/topic/new-path", pathViewFactory.createFrom(path));
    }
}
