package com.gft.application.file.watcher;

import com.gft.application.file.add.PathUtils;
import com.gft.watchservice.NotifySubscribers;
import com.gft.watchservice.OnSubscribeRegisterSubscriber;
import com.gft.watchservice.PollWatchServicePaths;
import com.gft.watchservice.RegistersPaths;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

public final class PathWatcherTask {

    private final Observer<Path> pathObserver;
    private final FileSystem fileSystem;
    private final PathUtils pathUtils;
    private final ExecutorService executorService;

    public PathWatcherTask(
        @NotNull final Observer<Path> pathObserver,
        @NotNull final FileSystem fileSystem,
        @NotNull final PathUtils pathUtils,
        @NotNull final ExecutorService executorService
    ) {
        this.pathObserver = pathObserver;
        this.fileSystem = fileSystem;
        this.pathUtils = pathUtils;
        this.executorService = executorService;
    }

    public void watch(@NotNull final Path path) throws IOException {
        WatchService watchService = fileSystem.newWatchService();
        pathUtils.registerDirectoriesRecursively(path, watchService);
        CopyOnWriteArrayList<Subscriber<? super Path>> subscribers = new CopyOnWriteArrayList<>();

        executorService.submit(
            new NotifySubscribers(
                subscribers,
                new RegistersPaths(new PollWatchServicePaths(watchService), watchService)
            )
        );

        Observable.create(new OnSubscribeRegisterSubscriber(subscribers)).subscribe(pathObserver);
    }
}
