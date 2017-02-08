package com.gft.path.watcher.async;

import com.gft.node.watcher.CouldNotCreatePayloadWatcher;
import com.gft.node.watcher.PayloadWatcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class AsyncPathWatcherFactory {

    private final FileSystem fileSystem;

    public AsyncPathWatcherFactory(@NotNull final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public AsyncPathWatcher create() {
        try {
            return new AsyncPathWatcher(fileSystem.newWatchService(), Executors.newSingleThreadExecutor());
        } catch (IOException e) {
            throw new CouldNotCreatePayloadWatcher("Could not create path watcher factory. Previous exception: " + e.getMessage(), e);
        }
    }
}
