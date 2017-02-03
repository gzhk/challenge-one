package com.gft.node;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class SimpleNode<P> implements Node<P> {

    private final P payload;
    private final List<Node<P>> children = new ArrayList<>();

    public SimpleNode(@NotNull final P payload) {
        this.payload = payload;
    }

    public void addChild(@NotNull Node<P> simpleNode) {
        children.add(simpleNode);
    }

    @Override
    public Collection<Node<P>> children() throws CannotRetrieveChildren {
        return new ArrayList<>(children);
    }

    @Override
    public P getPayload() {
        return payload;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof SimpleNode && Objects.equals(payload, ((SimpleNode) o).payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload);
    }
}
