package com.gft.node;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

final class BreadthFirstSearchNodeIterator<P> implements Iterator<P> {

    private final Queue<Node<P>> queue = new LinkedList<>();

    BreadthFirstSearchNodeIterator(@NotNull final Node<P> node) {
        this.queue.add(node);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public P next() {
        Node<P> node = queue.remove();

        try {
            node.children().forEach(queue::add);
        } catch (CannotRetrieveChildren e) {
            throw new NoSuchElementException(e.getMessage());
        }

        return node.getPayload();
    }
}
