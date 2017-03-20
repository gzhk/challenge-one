package com.gft.application.file.list;

import com.gft.application.file.model.PathViewFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

public final class SendCurrentPathsObserverTest {

    @Test
    public void sendsPathToWebSocket() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();
        Path path = mock(Path.class);

        UUID clientUUID = UUID.randomUUID();
        SendCurrentPathsObserver observer = new SendCurrentPathsObserver(clientUUID, pathViewFactory, simpMessagingTemplate, mock(Logger.class));
        observer.onNext(path);

        Map<String, Object> headers = new HashMap<>();
        headers.put("token", clientUUID);

        verify(simpMessagingTemplate, times(1)).convertAndSend("/topic/current-paths", pathViewFactory.createFrom(path), headers);
    }

    @Test
    public void logsStackTrace() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();

        Logger logger = mock(Logger.class);
        SendCurrentPathsObserver observer = new SendCurrentPathsObserver(UUID.randomUUID(), pathViewFactory, simpMessagingTemplate, logger);
        Exception exception = new Exception("some message");
        observer.onError(exception);

        verify(logger, times(1)).error("some message");
        verify(logger, times(1)).error(Arrays.toString(exception.getStackTrace()));

        verifyZeroInteractions(simpMessagingTemplate);
    }

}
