package com.gft.path;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

final class PollWatchServiceEvents implements Runnable {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;
    private final BlockingQueue<Path> pathQueue;

    PollWatchServiceEvents(
        @NotNull final WatchService watchService,
        @NotNull final ConcurrentMap<WatchKey, Path> keys,
        @NotNull final BlockingQueue<Path> pathQueue
    ) {
        this.watchService = watchService;
        this.keys = keys;
        this.pathQueue = pathQueue;
    }

    @Override
    public void run() {
        while (true) {
            final WatchKey watchKey;

            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            Path dir = keys.get(watchKey);

            watchKey.pollEvents()
                .stream()
                .filter(e -> (e.kind() != OVERFLOW))
                .map(e -> ((WatchEvent<Path>) e).context())
                .forEach(path -> {
                    try {
                        Files.walk(dir.resolve(path)).forEach(pathQueue::add);
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                });

            boolean valid = watchKey.reset();

            if (!valid) {
                break;
            }
        }
    }
}
