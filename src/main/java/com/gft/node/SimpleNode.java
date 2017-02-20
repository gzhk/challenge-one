package com.gft.node;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class SimpleNode<P> implements Node<P> {

    private final P payload;
    private final List<Node<P>> children;

    public SimpleNode(@NotNull final P payload) {
        this.payload = payload;
        this.children = new ArrayList<>();
    }

    public SimpleNode(@NotNull final P payload, @NotNull final List<Node<P>> children) {
        this.payload = payload;
        this.children = children;
    }

    @NotNull
    @Override
    public Collection<Node<P>> children() {
        return new ArrayList<>(children);
    }

    @NotNull
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
