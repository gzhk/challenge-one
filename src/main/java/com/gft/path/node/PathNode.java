package com.gft.path.node;

import com.gft.node.CannotRetrieveChildren;
import com.gft.node.Node;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PathNode implements Node<Path> {

    private final Path path;

    public PathNode(@NotNull final Path path) {
        this.path = path;
    }

    @Override
    public Collection<Node<Path>> children() throws CannotRetrieveChildren {
        try {
            return Files.list(path).map(PathNode::new).collect(Collectors.toList());
        } catch (IOException e) {
            throw new CannotRetrieveChildren("Cannot list files in path: " + path  , e);
        }
    }

    @Override
    public Path getPayload() {
        return path;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof PathNode && Objects.equals(path, ((PathNode) o).path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
