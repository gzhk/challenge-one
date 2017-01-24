package com.gft.path.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public final class PathService {

    public void registerPathInWatchService(final Path path, final WatchService watchService, final Map<WatchKey, Path> keys) {
        if (!path.toFile().exists() || !path.toFile().isDirectory()) {
            throw new RuntimeException("folder " + path + " does not exist or is not a directory");
        }

        try {
            Files.walkFileTree(path, new RecursiveWatchServiceFileVisitor(watchService, keys));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final class RecursiveWatchServiceFileVisitor extends SimpleFileVisitor<Path> {

        private final WatchService watchService;
        private final Map<WatchKey, Path> watchKeyPath;

        public RecursiveWatchServiceFileVisitor(WatchService watchService, Map<WatchKey, Path> watchKeyPath) {
            this.watchService = watchService;
            this.watchKeyPath = watchKeyPath;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            WatchKey watchKey = dir.register(watchService, ENTRY_CREATE);
            watchKeyPath.put(watchKey, dir);

            return FileVisitResult.CONTINUE;
        }
    }
}
