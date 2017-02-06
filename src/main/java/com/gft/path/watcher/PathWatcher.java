package com.gft.path.watcher;

import com.gft.path.treenode.PathTreeNode;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface PathWatcher extends AutoCloseable, Iterable<PathTreeNode> {

    void start();

    void registerPath(@NotNull Path path) throws CouldNotRegisterPath;
}
