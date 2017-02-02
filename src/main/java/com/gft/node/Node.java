package com.gft.node;

import java.util.Collection;

public interface Node<P> {

    Collection<Node<P>> children();

    P getPayload();
}
