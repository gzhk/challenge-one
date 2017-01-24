package com.gft.application.file.watcher;

import com.gft.application.file.list.ListService;
import com.gft.application.file.model.PathViewFactory;
import com.gft.path.treenode.PathTreeNode;
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
public final class PathWatcher {

    private final Path dir;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ListService listService;
    private final PathViewFactory pathViewFactory;

    @Autowired
    public PathWatcher(
        @Value("${dir}") String dir,
        SimpMessagingTemplate simpMessagingTemplate,
        ListService listService,
        PathViewFactory pathViewFactory
    ) {
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
                Observable<PathTreeNode> observableForPath = listService.createObservableForPath(dir);

                Observable.merge(observableForPath, observableForPath)
                    .subscribe(pathTreeNode -> {
                        simpMessagingTemplate.convertAndSend(
                            "/topic/new-path",
                            pathViewFactory.createFromPathTreeNode(pathTreeNode)
                        );
                    });
            });
    }
}
