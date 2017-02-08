package com.gft.node;

import com.gft.node.watcher.PayloadWatcher;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.observables.ConnectableObservable;

public final class NodePayloadIteratorObservableFactory implements NodePayloadObservableFactory {

    private final NodePayloadIterableFactory nodePayloadIterableFactory;

    public NodePayloadIteratorObservableFactory(@NotNull final NodePayloadIterableFactory nodePayloadIterableFactory) {
        this.nodePayloadIterableFactory = nodePayloadIterableFactory;
    }

    @Override
    public <T> ConnectableObservable<T> createForNode(@NotNull final Node<T> node) {
        return Observable.from(nodePayloadIterableFactory.createForNode(node)).publish();
    }

    @Override
    public <T> ConnectableObservable<T> createWithWatcher(
        @NotNull final Node<T> node,
        @NotNull final PayloadWatcher<T> payloadWatcher
    ) {
        Observable<T> observable = Observable.from(nodePayloadIterableFactory.createForNode(node))
            .mergeWith(Observable.from(payloadWatcher));

        observable.subscribe(payloadWatcher);

        return observable.publish();
    }
}
