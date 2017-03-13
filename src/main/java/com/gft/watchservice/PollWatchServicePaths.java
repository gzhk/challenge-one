package com.gft.watchservice;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Polls Path objects from WatchService.
 */
public final class PollWatchServicePaths {

    @NotNull
    public static List<Path> poll(@NotNull final WatchService watchService) {
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
