package com.gft.path;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public final class WatchServicePayloadRegistryFactory {

    private final FileSystem fileSystem;

    public WatchServicePayloadRegistryFactory(@NotNull final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public WatchServicePayloadRegistry create() throws CouldNotCreateWatchServicePayloadRegistry {
        try {
            return new WatchServicePayloadRegistry(
                Executors.newSingleThreadExecutor(),
                fileSystem.newWatchService(),
                new LinkedBlockingQueue<>(),
                new ConcurrentHashMap<>()
            );
        } catch (IOException e) {
            throw new CouldNotCreateWatchServicePayloadRegistry("Could not create new payload registry. ", e);
        }
    }
}
