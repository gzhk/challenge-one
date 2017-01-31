package com.gft.path.watcher;

import com.gft.path.treenode.PathTreeNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.BlockingQueue;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class PollWatchServiceEvents implements Runnable {

    @NotNull
    private final Path rootPath;
    private final WatchService watchService;
    private final BlockingQueue<PathTreeNode> pathQueue;

    public PollWatchServiceEvents(@NotNull Path rootPath, @NotNull WatchService watchService, @NotNull BlockingQueue<PathTreeNode> pathQueue) {
        this.rootPath = rootPath;
        this.watchService = watchService;
        this.pathQueue = pathQueue;
    }

    @Override
    public void run() {
        while (true) {
            final WatchKey key;

            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            key.pollEvents()
                .stream()
                .filter(e -> (e.kind() != OVERFLOW))
                .map(e -> ((WatchEvent<Path>) e).context())
                .forEach(path -> {
                    try {
                        Files.walkFileTree(rootPath.resolve(path), new RegisterPaths(rootPath, pathQueue));
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                });

            boolean valid = key.reset();

            if (!valid) {
                break;
            }
        }
    }

    private static class RegisterPaths extends SimpleFileVisitor<Path> {

        private final Path rootPath;
        private final BlockingQueue<PathTreeNode> pathQueue;

        RegisterPaths(@NotNull final Path rootPath, @NotNull final BlockingQueue<PathTreeNode> pathQueue) {
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
                if (path.equals(rootPath)) {
                    pathQueue.put(new PathTreeNode(path));
                } else {
                    pathQueue.put(new PathTreeNode(path, new PathTreeNode(path.getParent())));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
