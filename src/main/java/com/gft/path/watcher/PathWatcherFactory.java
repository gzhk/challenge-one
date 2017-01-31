package com.gft.path.watcher;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.concurrent.Executors;

public final class PathWatcherFactory {

    private final FileSystem fileSystem;

    public PathWatcherFactory(@NotNull final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public PathWatcher create() throws CouldNotCreatePathWatcher {
        try {
            return new PathWatcher(fileSystem.newWatchService(), Executors.newSingleThreadExecutor());
        } catch (IOException e) {
            throw new CouldNotCreatePathWatcher("Could not create path watcher factory. Previous exception: " + e.getMessage(), e);
        }
    }
}
