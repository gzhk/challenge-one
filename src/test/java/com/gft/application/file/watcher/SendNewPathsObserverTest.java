package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.file.Path;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class SendNewPathsObserverTest {

    @Test
    public void sendsPathToWebSocket() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();
        Path path = mock(Path.class);

        SendNewPathsObserver sendNewPathsObserver = new SendNewPathsObserver(pathViewFactory, simpMessagingTemplate, mock(Logger.class));
        sendNewPathsObserver.onNext(path);

        verify(simpMessagingTemplate, times(1)).convertAndSend("/topic/new-path", pathViewFactory.createFrom(path));
    }

    @Test
    public void doesNoting() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();
        Logger logger = mock(Logger.class);

        SendNewPathsObserver sendNewPathsObserver = new SendNewPathsObserver(pathViewFactory, simpMessagingTemplate, logger);
        sendNewPathsObserver.onCompleted();

        verifyZeroInteractions(simpMessagingTemplate, logger);
    }

    @Test
    public void logsStackTrace() throws Exception {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        PathViewFactory pathViewFactory = new PathViewFactory();

        Logger logger = mock(Logger.class);
        SendNewPathsObserver sendNewPathsObserver = new SendNewPathsObserver(pathViewFactory, simpMessagingTemplate, logger);
        Exception exception = new Exception("some message");
        sendNewPathsObserver.onError(exception);

        verify(logger, times(1)).error("some message");
        verify(logger, times(1)).error(Arrays.toString(exception.getStackTrace()));

        verifyZeroInteractions(simpMessagingTemplate);
    }
}
