package com.gft.watchservice;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

@FunctionalInterface
public interface WatchServicePaths {

    @NotNull
    List<Path> poll();
}
