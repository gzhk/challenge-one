package com.gft.path.watcher.async;

import com.gft.collections.BlockingQueueIterator;
import com.gft.node.watcher.CouldNotRegisterPayload;
import com.gft.node.watcher.PayloadWatcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class AsyncPathWatcher implements PayloadWatcher<Path> {

    private final WatchService watchService;
    private final ConcurrentMap<WatchKey, Path> keys;
    private final BlockingQueue<Path> pathQueue;

    public AsyncPathWatcher(
        @NotNull final WatchService watchService,
        @NotNull final ConcurrentMap<WatchKey, Path> keys,
        @NotNull final BlockingQueue<Path> pathQueue
    ) {
        this.watchService = watchService;
        this.keys = keys;
        this.pathQueue = pathQueue;
    }

    @Override
    public Iterator<Path> iterator() {
        return new BlockingQueueIterator<>(pathQueue);
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
}
