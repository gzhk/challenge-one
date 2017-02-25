package com.gft.path.iterator;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;

public final class RegistersPaths implements WatchServiceIterator {

    private final WatchServiceIterator inner;
    private final WatchService watchService;

    public RegistersPaths(@NotNull final WatchServiceIterator inner, @NotNull final WatchService watchService) {
        this.inner = inner;
        this.watchService = watchService;
    }

    @Override
    public boolean hasNext() {
        return inner.hasNext();
    }

    @Override
    public Path next() {
        final Path path = inner.next();

        if (Files.isDirectory(path)) {
            try {
                path.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);
            } catch (IOException e) {
                throw new RuntimeException("Could not register path.", e);
            }
        }

        return path;
    }

    @Override
    public void close() throws Exception {
        inner.close();
        watchService.close();
    }
}
