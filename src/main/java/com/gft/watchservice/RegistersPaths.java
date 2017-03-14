package com.gft.watchservice;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * Passes further path and registers it if the Path is a directory.
 */
public final class RegistersPaths {

    public static void register(@NotNull final Stream<Path> paths, @NotNull final WatchService watchService) {
        paths
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
    }
}
