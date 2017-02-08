package com.gft.collections;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

public final class BlockingQueueIterator<E> implements Iterator<E> {

    private final BlockingQueue<E> queue;

    public BlockingQueueIterator(@NotNull final BlockingQueue<E> queue) {
        this.queue = queue;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public E next() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not return next element, because thread has been interrupted.", e);
        }
    }
}
