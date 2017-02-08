package com.gft.node.watcher;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Queue;

public final class QueuePayloadWatcher<T> implements PayloadWatcher<T> {

    private final Queue<T> queue;

    public QueuePayloadWatcher(@NotNull final Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void call(final T payload) {
        queue.add(payload);
    }

    @Override
    public Iterator<T> iterator() {
        return queue.iterator();
    }
}
