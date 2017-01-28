package com.gft.path.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public final class RecursivePathWatcher {

    private final Path path;
    private final WatchService watchService;
    private final ExecutorService executor;
    private final PathService pathService;
    private final BlockingQueue<Path> pathQueue = new ArrayBlockingQueue<>(16);

    public RecursivePathWatcher(Path path, WatchService watchService, ExecutorService executor, PathService pathService) {
        this.path = path;
        this.watchService = watchService;
        this.executor = executor;
        this.pathService = pathService;
    }

    public void start() throws IOException {
        final Object lock = new Object();
        pathService.registerPathInWatchService(path, watchService);
        executor.submit(new PollWatchServiceEvents(watchService, pathQueue, pathService, lock));
    }

    public void close() throws IOException {
        watchService.close();
        executor.shutdown();
    }
}
