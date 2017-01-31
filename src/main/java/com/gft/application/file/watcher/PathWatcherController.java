package com.gft.application.file.watcher;

import com.gft.application.file.model.PathViewFactory;
import com.gft.path.PathTreeNodeObservableFactory;
import com.gft.path.watcher.CouldNotRegisterPath;
import com.gft.path.watcher.PathWatcher;
import com.gft.path.watcher.PathWatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import rx.Observable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

@Controller
public final class PathWatcherController {

    private final Path dir;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PathTreeNodeObservableFactory pathTreeNodeObservableFactory;
    private final PathViewFactory pathViewFactory;
    private final PathWatcherFactory pathWatcherFactory;

    @Autowired
    public PathWatcherController(
        @Value("${dir}") String dir,
        SimpMessagingTemplate simpMessagingTemplate,
        PathTreeNodeObservableFactory pathTreeNodeObservableFactory,
        PathViewFactory pathViewFactory,
        PathWatcherFactory pathWatcherFactory
    ) {
        this.dir = Paths.get(dir);
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.pathTreeNodeObservableFactory = pathTreeNodeObservableFactory;
        this.pathViewFactory = pathViewFactory;
        this.pathWatcherFactory = pathWatcherFactory;
        start();
    }

    @MessageMapping("/current-paths")
    public void currentPaths() throws Exception {
        pathTreeNodeObservableFactory
            .createObservableForPath(dir)
            .subscribe(pathTreeNode ->
                simpMessagingTemplate.convertAndSend(
                    "/topic/new-path",
                    pathViewFactory.createFromPathTreeNode(pathTreeNode)
                )
            );
    }

    private void start() {
        Executors
            .newSingleThreadExecutor()
            .submit(() -> {
                try (PathWatcher pathWatcher = pathWatcherFactory.create()) {
                    pathWatcher.start(dir);

                    Observable
                        .merge(pathTreeNodeObservableFactory.createObservableForPath(dir), Observable.from(pathWatcher))
                        .subscribe(pathTreeNode -> {
                            try {
                                pathWatcher.registerPath(pathTreeNode.getPath());
                            } catch (CouldNotRegisterPath e) {
                                throw new RuntimeException("Could not register path.", e);
                            }

                            simpMessagingTemplate.convertAndSend(
                                "/topic/new-path",
                                pathViewFactory.createFromPathTreeNode(pathTreeNode)
                            );
                        });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
