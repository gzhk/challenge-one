package com.gft.node;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

final class NodePayloadIterator<P> implements Iterator<P> {

    private final Queue<Node<P>> queue = new LinkedList<>();

    NodePayloadIterator(@NotNull final Node<P> node) {
        this.queue.add(node);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public P next() {
        Node<P> node = queue.remove();
        node.children().forEach(queue::add);

        return node.getPayload();
    }
}
