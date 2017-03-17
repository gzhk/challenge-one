package com.gft.watchservice;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class WatchServiceFunctions {

    public static void registerPaths(@NotNull final Stream<Path> paths, @NotNull final WatchService watchService) {
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

    @NotNull
    public static List<Path> pollPaths(@NotNull final WatchService watchService) {
        final WatchKey watchKey;

        watchKey = watchService.poll();

        if (watchKey == null) {
            return Collections.emptyList();
        }

        List<Path> result =
            watchKey
                .pollEvents()
                .stream()
                .filter(watchEvent -> watchEvent.kind() != OVERFLOW)
                .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
                .map(path -> ((Path) watchKey.watchable()).resolve(path))
                .flatMap(path -> {
                    try {
                        return Files.walk(path);
                    } catch (IOException e) {
                        throw new CouldNotReadRootPath("Could not read root path: " + path + ".", e);
                    }
                })
                .collect(Collectors.toList());

        watchKey.reset();

        return result;
    }
}
