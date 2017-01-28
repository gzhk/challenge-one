package com.gft.path.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public final class PathService {

    public void registerPathInWatchService(final Path path, final WatchService watchService) {
        if (!path.toFile().exists() || !path.toFile().isDirectory()) {
            throw new RuntimeException("folder " + path + " does not exist or is not a directory");
        }

        try {
            Files.walkFileTree(path, new RecursiveWatchServiceFileVisitor(watchService));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final class RecursiveWatchServiceFileVisitor extends SimpleFileVisitor<Path> {

        private final WatchService watchService;

        public RecursiveWatchServiceFileVisitor(WatchService watchService) {
            this.watchService = watchService;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            dir.register(watchService, ENTRY_CREATE);

            return FileVisitResult.CONTINUE;
        }
    }
}
