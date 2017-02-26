package com.gft.watchservice.iterator;

import org.jetbrains.annotations.NotNull;

import java.nio.file.WatchService;

public final class RegistersPolledWatchServicePathsFactory implements WatchServiceIteratorFactory {

    @NotNull
    @Override
    public WatchServiceIterator create(@NotNull WatchService watchService) {
        return new RegistersPaths(new PollsWatchServicePaths(watchService), watchService);
    }
}
