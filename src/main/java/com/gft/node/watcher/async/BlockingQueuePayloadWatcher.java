package com.gft.node.watcher.async;

import com.gft.collections.BlockingQueueIterator;
import com.gft.node.watcher.PayloadWatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class BlockingQueuePayloadWatcher<T> implements PayloadWatcher<T> {

    private final Registry<T> registry;

    public BlockingQueuePayloadWatcher(@NotNull final Registry<T> registry) {
        this.registry = registry;
    }

    @Override
    public Iterator<T> iterator() {
        return new BlockingQueueIterator<>(registry.queue());
    }

    @Override
    public void call(final T payload) {
        registry.watch(payload);
    }
}
