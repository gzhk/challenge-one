package com.gft.path.iterator;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class PollsWatchServicePaths implements WatchServiceIterator {

    private final WatchService watchService;
    private final Queue<Path> paths = new LinkedList<>();
    private boolean hasNext = true;

    public PollsWatchServicePaths(@NotNull final WatchService watchService) {
        this.watchService = watchService;
    }

    @Override
    public boolean hasNext() {
        if (!hasNext) {
            return false;
        }

        if (paths.isEmpty()) {
            paths.addAll(pollPaths(watchService));
        }

        return hasNext && !paths.isEmpty();
    }

    @Override
    public Path next() {
        return paths.remove();
    }

    private List<Path> pollPaths(final WatchService watchService) {
        final WatchKey watchKey;

        try {
            watchKey = watchService.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final List<Path> result = new ArrayList<>();

        watchKey.pollEvents()
            .stream()
            .filter(watchEvent -> watchEvent.kind() != OVERFLOW)
            .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
            .map(path -> ((Path) watchKey.watchable()).resolve(path))
            .forEach(path -> {
                try {
                    Files.walk(path).forEach(result::add);
                } catch (IOException e) {
                    throw new RuntimeException("Could not read root path.", e);
                }
            });

        if (!watchKey.reset()) {
            hasNext = false;
        }

        return result;
    }

    @Override
    public void close() throws Exception {
        hasNext = false;
        watchService.close();
    }
}
