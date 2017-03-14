package com.gft.application.file.watcher;

import com.gft.watchservice.NotifySubscribers;
import com.gft.watchservice.OnSubscribeRegisterSubscriber;
import com.gft.watchservice.RegistersPaths;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PathWatcherTask implements AutoCloseable {

    private final FileSystem fileSystem;
    private final ExecutorService executorService;
    private final List<WatchService> watchServices = new ArrayList<>();

    public PathWatcherTask(@NotNull final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void watch(@NotNull final Path path, @NotNull final Observer<Path> pathObserver) throws IOException {
        WatchService watchService = fileSystem.newWatchService();
        watchServices.add(watchService);
        RegistersPaths.register(Files.walk(path), watchService);
        CopyOnWriteArrayList<Subscriber<? super Path>> subscribers = new CopyOnWriteArrayList<>();
        Observable
            .create(new OnSubscribeRegisterSubscriber(subscribers))
            .doOnUnsubscribe(() -> subscribers.removeIf(Subscriber::isUnsubscribed))
            .subscribe(pathObserver);
        executorService.submit(new NotifySubscribers(watchService, subscribers));
    }

    public void close() throws Exception {
        executorService.shutdownNow();
        closeWatchServices();
    }

    private void closeWatchServices() throws IOException {
        ArrayList<WatchService> watchServices = new ArrayList<>(this.watchServices);
        this.watchServices.clear();
        for (WatchService watchService : watchServices) {
            watchService.close();
        }
    }
}
