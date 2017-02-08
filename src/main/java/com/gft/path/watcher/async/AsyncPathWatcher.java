package com.gft.path.watcher.async;

import com.gft.node.watcher.CouldNotRegisterPayload;
import com.gft.node.watcher.PayloadWatcher;
import com.gft.collections.BlockingQueueIterator;
import com.gft.path.watcher.PollWatchServiceEvents;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.concurrent.*;

public class AsyncPathWatcher implements PayloadWatcher<Path>, AutoCloseable {

    private final WatchService watchService;
    private final ExecutorService executorService;
    private final BlockingQueue<Path> newPathsQueue = new LinkedBlockingQueue<>();
    private final ConcurrentMap<WatchKey, Path> keys = new ConcurrentHashMap<>();

    public AsyncPathWatcher(@NotNull final WatchService watchService, @NotNull final ExecutorService executorService) {
        this.watchService = watchService;
        this.executorService = executorService;
        init();
    }

    private void init() {
        this.executorService.submit(new PollWatchServiceEvents(watchService, keys, newPathsQueue));
    }

    @Override
    public void call(final Path path) {
        if (!Files.isDirectory(path)) {
            return;
        }

        try {
            WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            keys.put(watchKey, path);
        } catch (IOException e) {
            throw new CouldNotRegisterPayload("Could not register path " + path, e);
        }
    }

    @Override
    public Iterator<Path> iterator() {
        return new BlockingQueueIterator<>(newPathsQueue);
    }

    @Override
    public void close() throws Exception {
        watchService.close();
        executorService.shutdown();
    }
}
