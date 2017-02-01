package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.path.CannotCreateObservable;
import com.gft.path.PathTreeNodeObservableFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public final class PathWatcherController {

    private final Path dir;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PathTreeNodeObservableFactory pathTreeNodeObservableFactory;
    private final PathViewFactory pathViewFactory;

    @Autowired
    public PathWatcherController(
        @Value("${dir}") String dir,
        SimpMessagingTemplate simpMessagingTemplate,
        PathTreeNodeObservableFactory pathTreeNodeObservableFactory,
        PathViewFactory pathViewFactory
    ) {
        this.dir = Paths.get(dir);
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.pathTreeNodeObservableFactory = pathTreeNodeObservableFactory;
        this.pathViewFactory = pathViewFactory;
    }

    @MessageMapping("/current-paths")
    public void currentPaths() throws CannotCreateObservable {
        pathTreeNodeObservableFactory
            .createObservableForPath(dir)
            .subscribe(pathTreeNode ->
                simpMessagingTemplate.convertAndSend(
                    "/topic/new-path",
                    pathViewFactory.createFromPathTreeNode(pathTreeNode)
                )
            );
    }
}
