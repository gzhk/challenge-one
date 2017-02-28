package com.gft.watchservice;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

public interface WatchServicePaths {

    @NotNull
    List<Path> poll();
}
