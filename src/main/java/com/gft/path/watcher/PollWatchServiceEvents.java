package com.gft.path.watcher;

import org.jetbrains.annotations.NotNull;

import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class PollWatchServiceEvents implements Runnable {

    private final WatchService watchService;
    private final BlockingQueue<Path> paths;
    private final PathService pathService;
    private final Object lock;

    public PollWatchServiceEvents(
        @NotNull WatchService watchService,
        @NotNull BlockingQueue<Path> newPaths,
        @NotNull PathService pathService,
        @NotNull Object lock
    ) {
        this.watchService = watchService;
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

            key.pollEvents()
                .stream()
                .filter(e -> (e.kind() != OVERFLOW))
                .map(e -> ((WatchEvent<Path>) e).context())
                .forEach(p -> {
                    final Path absPath = p.toAbsolutePath();

                    if (Files.isDirectory(absPath)) {
                        synchronized (lock) {
                            pathService.registerPathInWatchService(absPath, watchService);
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
