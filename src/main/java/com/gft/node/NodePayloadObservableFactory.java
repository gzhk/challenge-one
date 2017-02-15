package com.gft.node;

import com.gft.node.watcher.PayloadRegistry;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class NodePayloadObservableFactory {

    private final NodePayloadIterableFactory nodePayloadIterableFactory;

    public NodePayloadObservableFactory(@NotNull final NodePayloadIterableFactory nodePayloadIterableFactory) {
        this.nodePayloadIterableFactory = nodePayloadIterableFactory;
    }

    public <T> ConnectableObservable<T> createForNode(@NotNull final Node<T> node) {
        return Observable.from(nodePayloadIterableFactory.createForNode(node))
            .subscribeOn(Schedulers.newThread())
            .publish();
    }

    public <T> ConnectableObservable<T> createWithIncludedChanges(
        @NotNull final Node<T> node,
        @NotNull final PayloadRegistry<T> payloadRegistry
    ) {
        ConnectableObservable<T> observable = Observable.from(nodePayloadIterableFactory.createForNode(node))
            .mergeWith(payloadRegistry.changes())
            .subscribeOn(Schedulers.newThread())
            .publish();

        observable.subscribe(payloadRegistry::registerPayload);

        return observable;
    }
}
