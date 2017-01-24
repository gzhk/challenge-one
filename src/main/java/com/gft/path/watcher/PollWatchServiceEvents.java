package com.gft.path.watcher;

import org.jetbrains.annotations.NotNull;

import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class PollWatchServiceEvents implements Runnable {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;
    private final BlockingQueue<Path> paths;
    private final PathService pathService;
    private final Object lock;

    public PollWatchServiceEvents(
        @NotNull WatchService watchService,
        @NotNull Map<WatchKey, Path> keys,
        @NotNull BlockingQueue<Path> newPaths,
        @NotNull PathService pathService,
        @NotNull Object lock
    ) {
        this.watchService = watchService;
        this.keys = keys;
        this.paths = newPaths;
        this.pathService = pathService;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (true) {
            final WatchKey key;

            try {
                key = watchService.take();
            } catch (InterruptedException ex) {
                return;
            }

            final Path dir = keys.get(key);

            if (dir == null) {
                System.err.println("WatchKey " + key + " not recognized!");
                continue;
            }

            key.pollEvents()
                .stream()
                .filter(e -> (e.kind() != OVERFLOW))
                .map(e -> ((WatchEvent<Path>) e).context())
                .forEach(p -> {
                    final Path absPath = dir.resolve(p);

                    if (Files.isDirectory(absPath)) {
                        synchronized (lock) {
                            pathService.registerPathInWatchService(absPath, watchService, keys);
                        }
                    } else {
                        System.out.println("path: " + p);
                        paths.add(p);
                    }

                    System.out.println("abs: " + absPath);

                    paths.add(p);
                });

            boolean valid = key.reset();

            if (!valid) {
                break;
            }
        }
    }
}
