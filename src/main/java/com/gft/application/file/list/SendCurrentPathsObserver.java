package com.gft.application.file.list;

import com.gft.application.file.model.PathViewFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observer;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class SendCurrentPathsObserver implements Observer<Path> {

    private final UUID clientUUID;
    private final PathViewFactory pathViewFactory;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Logger logger;

    public SendCurrentPathsObserver(
        @NotNull final UUID clientUUID,
        @NotNull final PathViewFactory pathViewFactory,
        @NotNull final SimpMessagingTemplate simpMessagingTemplate,
        @NotNull final Logger logger
    ) {
        this.clientUUID = clientUUID;
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
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("token", clientUUID);

        simpMessagingTemplate.convertAndSend("/topic/current-paths", pathViewFactory.createFrom(path), headers);
    }
}
