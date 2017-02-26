package com.gft.watchservice;

import org.jetbrains.annotations.NotNull;

import java.nio.file.WatchService;

@FunctionalInterface
public interface WatchServiceFactory {

    @NotNull
    WatchService create();
}
