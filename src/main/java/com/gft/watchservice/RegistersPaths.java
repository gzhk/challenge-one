package com.gft.watchservice;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Passes further path and registers it if the Path is a directory.
 */
public final class RegistersPaths implements WatchServicePaths {

    private final WatchServicePaths inner;
    private final WatchService watchService;

    public RegistersPaths(
        @NotNull final WatchServicePaths inner,
        @NotNull final WatchService watchService
    ) {
        this.inner = inner;
        this.watchService = watchService;
    }

    @NotNull
    @Override
    public List<Path> poll() {
        List<Path> paths = inner.poll();

        paths
            .stream()
            .filter(path -> Files.isDirectory(path))
            .forEach(path -> {
                try {
                    path.register(
                        watchService,
                        new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE},
                        SensitivityWatchEventModifier.HIGH
                    );
                } catch (IOException e) {
                    throw new CouldNotRegisterPath("Could not register path: " + path, e);
                }
            });

        return paths;
    }
}
