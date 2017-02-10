package com.gft.node;

import com.gft.node.watcher.PayloadRegistry;
import org.jetbrains.annotations.NotNull;
import rx.observables.ConnectableObservable;

public interface NodePayloadObservableFactory {

    <T> ConnectableObservable<T> createForNode(@NotNull Node<T> node);

    <T> ConnectableObservable<T> createWithWatcher(@NotNull Node<T> node, @NotNull final PayloadRegistry<T> payloadRegistry);
}
