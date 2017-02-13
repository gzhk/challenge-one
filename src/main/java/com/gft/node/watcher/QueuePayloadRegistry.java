package com.gft.node.watcher;

import org.jetbrains.annotations.NotNull;
import rx.Observable;

import java.util.List;

/**
 * Dummy implementation of {@link PayloadRegistry}.
 * It is emitting registered elements.
 *
 * @param <T> Type of elements that can be registered in the registry.
 */
public final class QueuePayloadRegistry<T> implements PayloadRegistry<T> {

    private final List<T> list;
    private final List<T> changes;

    public QueuePayloadRegistry(@NotNull final List<T> list, @NotNull final List<T> changes) {
        this.list = list;
        this.changes = changes;
    }

    @Override
    public void registerPayload(final T payload) {
        list.add(payload);
    }

    @Override
    public Observable<T> changes() {
        return Observable.from(changes);
    }
}
