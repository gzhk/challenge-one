package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.file.Path;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class SendPathViewObserverTest {

    @Test
    public void sendsPathToWebSocket() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();
        Path path = mock(Path.class);

        SendPathViewObserver sendPathViewObserver = new SendPathViewObserver(pathViewFactory, simpMessagingTemplate, mock(Logger.class));
        sendPathViewObserver.onNext(path);

        verify(simpMessagingTemplate, times(1)).convertAndSend("/topic/new-path", pathViewFactory.createFrom(path));
    }

    @Test
    public void doesNoting() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();
        Logger logger = mock(Logger.class);

        SendPathViewObserver sendPathViewObserver = new SendPathViewObserver(pathViewFactory, simpMessagingTemplate, logger);
        sendPathViewObserver.onCompleted();

        verifyZeroInteractions(simpMessagingTemplate, logger);
    }

    @Test
    public void logsStackTrace() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();

        Logger logger = mock(Logger.class);
        SendPathViewObserver sendPathViewObserver = new SendPathViewObserver(pathViewFactory, simpMessagingTemplate, logger);
        Exception exception = new Exception("some message");
        sendPathViewObserver.onError(exception);

        verify(logger, times(1)).error("some message");
        verify(logger, times(1)).error(Arrays.toString(exception.getStackTrace()));

        verifyZeroInteractions(simpMessagingTemplate);
    }
}
