package com.gft.application.file.watcher;

import com.gft.application.file.list.ListService;
import com.gft.application.file.model.PathViewFactory;
import com.gft.path.treenode.PathTreeNode;
import com.gft.path.watcher.CouldNotRegisterPath;
import com.gft.path.watcher.PathWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import rx.Observable;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public final class PathWatcherController {

    private final Path dir;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ListService listService;
    private final PathViewFactory pathViewFactory;

    @Autowired
    public PathWatcherController(
        @Value("${dir}") String dir,
        SimpMessagingTemplate simpMessagingTemplate,
        ListService listService,
        PathViewFactory pathViewFactory
    ) throws Exception {
        this.dir = Paths.get(dir);
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.listService = listService;
        this.pathViewFactory = pathViewFactory;
        start();
    }

    @MessageMapping("/current-paths")
    public void currentPaths() {
        listService
            .createObservableForPath(dir)
            .subscribe(pathTreeNode -> {
                simpMessagingTemplate.convertAndSend(
                    "/topic/new-path",
                    pathViewFactory.createFromPathTreeNode(pathTreeNode)
                );
            });
    }

    private void start() {
        Executors
            .newSingleThreadExecutor()
            .submit(() -> {
                WatchService watchService;
                try {
                    watchService = FileSystems.getDefault().newWatchService();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ExecutorService executorService = Executors.newFixedThreadPool(12);
                PathWatcher pathWatcher = new PathWatcher(watchService, executorService);
                pathWatcher.start(dir);

                Observable<PathTreeNode> observableForPath = listService.createObservableForPath(dir);
                try {
                    pathWatcher.registerPath(dir);
                } catch (CouldNotRegisterPath e) {
                    e.printStackTrace();
                }

//                    Observable.merge(observableForPath, Observable.from(pathWatcher))
                Observable.from(pathWatcher)
//                observableForPath
                    .subscribe(pathTreeNode -> {
                        System.out.println(pathTreeNode.getPath());

                        try {
                            pathWatcher.registerPath(pathTreeNode.getPath());
                        } catch (CouldNotRegisterPath e) {
                            throw new RuntimeException("Could not register path.", e);
                        }

                        simpMessagingTemplate.convertAndSend(
                            "/topic/new-path",
                            pathViewFactory.createFromPathTreeNode(pathTreeNode)
                        );

                        System.out.println("send "+ pathTreeNode);
                    });

                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$ not observing anymore");
            });
    }
}
