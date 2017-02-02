package com.gft.path.node;

import com.gft.node.Node;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;

public final class PathNode implements Node<Path> {

    private final Path path;

    public PathNode(@NotNull final Path path) {
        this.path = path;
    }

    @Override
    public Collection<Node<Path>> children() {
        return null;
    }

    @Override
    public Path getPayload() {
        return path;
    }
}
