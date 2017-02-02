package com.gft.node;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

final class BreadthFirstSearchNodeIterator<P> implements Iterator<P> {

    private final Queue<Node<P>> queue = new LinkedList<>();

    BreadthFirstSearchNodeIterator(@NotNull final Node<P> node) {
        queue.add(node);
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
