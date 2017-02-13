package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import rx.Observer;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SendPathViewObserver implements Observer<Path> {

    private final PathViewFactory pathViewFactory;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Logger logger;

    public SendPathViewObserver(
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
        logger.log(Level.WARNING, e.getMessage());
        logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
    }

    @Override
    public void onNext(final Path path) {
        simpMessagingTemplate.convertAndSend("/topic/new-path", pathViewFactory.createFrom(path));
    }
}
