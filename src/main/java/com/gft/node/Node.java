package com.gft.node;

import java.util.Collection;

public interface Node<P> {

    Collection<Node<P>> children() throws CannotRetrieveChildren;

    P getPayload();
}
