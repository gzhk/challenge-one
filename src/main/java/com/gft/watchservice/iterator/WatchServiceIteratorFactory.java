package com.gft.watchservice.iterator;

import org.jetbrains.annotations.NotNull;

import java.nio.file.WatchService;

@FunctionalInterface
public interface WatchServiceIteratorFactory {

    @NotNull
    WatchServiceIterator create(@NotNull final WatchService watchService);
}
