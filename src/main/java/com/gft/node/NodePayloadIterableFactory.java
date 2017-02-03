package com.gft.node;

import org.jetbrains.annotations.NotNull;

public final class NodePayloadIterableFactory {

    public <P> Iterable<P> createForNode(@NotNull final Node<P> node) {
        return () -> new BreadthFirstSearchNodeIterator<>(node);
    }
}
