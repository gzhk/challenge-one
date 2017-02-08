package com.gft.path.watcher;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class PollWatchServiceEvents implements Runnable {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;
    private final BlockingQueue<Path> pathQueue;

    public PollWatchServiceEvents(
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
                        Files.walkFileTree(dir.resolve(path), new RegisterPaths(dir, pathQueue));
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

    private static class RegisterPaths extends SimpleFileVisitor<Path> {

        private final Path rootPath;
        private final BlockingQueue<Path> pathQueue;

        RegisterPaths(@NotNull final Path rootPath, @NotNull final BlockingQueue<Path> pathQueue) {
            this.rootPath = rootPath;
            this.pathQueue = pathQueue;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            registerPath(rootPath.resolve(dir));

            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            registerPath(rootPath.resolve(file));

            return super.visitFile(file, attrs);
        }

        private void registerPath(Path path) {
            try {
                pathQueue.put(path);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
