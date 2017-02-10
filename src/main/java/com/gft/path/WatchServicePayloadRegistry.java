package com.gft.path;

import com.gft.collections.BlockingQueueIterator;
import com.gft.node.watcher.CouldNotRegisterPayload;
import com.gft.node.watcher.PayloadRegistry;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public final class WatchServicePayloadRegistry implements PayloadRegistry<Path> {

    private final ExecutorService executorService;
    private final WatchService watchService;
    private final BlockingQueue<Path> newPaths;
    private final ConcurrentMap<WatchKey, Path> keys;

    public WatchServicePayloadRegistry(
        @NotNull final ExecutorService executorService,
        @NotNull final WatchService watchService,
        @NotNull final BlockingQueue<Path> newPaths,
        @NotNull final ConcurrentMap<WatchKey, Path> keys
    ) {
        this.executorService = executorService;
        this.watchService = watchService;
        this.newPaths = newPaths;
        this.keys = keys;
    }

    public void startWatching() {
        executorService.submit(new PollWatchServiceEvents(watchService, keys, newPaths));
    }

    @Override
    public void registerPayload(final Path path) {
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
    public Observable<Path> changes() {
        return Observable.from(() -> new BlockingQueueIterator<>(newPaths));
    }
}
