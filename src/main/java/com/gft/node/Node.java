package com.gft.node;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Node<P> {

    /**
     * @return Collection of children or empty collection if does not have children.
     * @throws CannotRetrieveChildren
     */
    @NotNull
    Collection<Node<P>> children();

    @NotNull
    P getPayload();
}
