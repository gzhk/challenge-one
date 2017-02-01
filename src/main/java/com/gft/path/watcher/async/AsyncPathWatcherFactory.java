package com.gft.path.watcher.async;

import com.gft.path.watcher.CouldNotCreatePathWatcher;
import com.gft.path.watcher.PathWatcher;
import com.gft.path.watcher.PathWatcherFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.concurrent.Executors;

public class AsyncPathWatcherFactory implements PathWatcherFactory {

    private final FileSystem fileSystem;

    public AsyncPathWatcherFactory(@NotNull final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public PathWatcher create() throws CouldNotCreatePathWatcher {
        try {
            return new AsyncPathWatcher(fileSystem.newWatchService(), Executors.newSingleThreadExecutor());
        } catch (IOException e) {
            throw new CouldNotCreatePathWatcher("Could not create path watcher factory. Previous exception: " + e.getMessage(), e);
        }
    }
}
