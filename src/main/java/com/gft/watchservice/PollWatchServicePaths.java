package com.gft.watchservice;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Polls Path objects from WatchService passed during initialization.
 */
public final class PollWatchServicePaths implements WatchServicePaths {

    private final WatchService watchService;

    public PollWatchServicePaths(@NotNull final WatchService watchService) {
        this.watchService = watchService;
    }

    @NotNull
    @Override
    public List<Path> poll() {
        final WatchKey watchKey;

        watchKey = watchService.poll();

        if (watchKey == null) {
            return Collections.emptyList();
        }

        final List<Path> result = new ArrayList<>();

        watchKey.pollEvents()
            .stream()
            .filter(watchEvent -> watchEvent.kind() != OVERFLOW)
            .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
            .map(path -> ((Path) watchKey.watchable()).resolve(path))
//            .flatMap(path -> { // TODO
//                try {
//                    return Files.walk(path);
//                } catch (IOException e) {
//                    throw new RuntimeException("Could not read root path.", e);
//                }
//            });
            .forEach(path -> {
                try {
                    Files.walk(path).forEach(result::add);
                } catch (IOException e) {
                    throw new RuntimeException("Could not read root path.", e);
                }
            });

        watchKey.reset();

        return result;
    }
}
