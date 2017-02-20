package com.gft.node;

import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class NodePayloadObservableFactory {

    private final NodePayloadIterableFactory nodePayloadIterableFactory;

    public NodePayloadObservableFactory(@NotNull final NodePayloadIterableFactory nodePayloadIterableFactory) {
        this.nodePayloadIterableFactory = nodePayloadIterableFactory;
    }

    /**
     * @param rootNode Root node of tree structure.
     * @param <T> Type of the node payload.
     * @return ConnectableObservable witch emits children from tree structure for given root node.
     */
    public <T> ConnectableObservable<T> create(@NotNull final Node<T> rootNode) {
        return Observable.from(nodePayloadIterableFactory.createForNode(rootNode)).publish();
    }
}
