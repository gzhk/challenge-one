package com.gft.path.node;

import com.gft.node.Node;
import com.gft.node.NodePayloadIterableFactory;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public final class PathPayloadObservableFactory {

    private final NodePayloadIterableFactory nodePayloadIterableFactory;

    public PathPayloadObservableFactory(@NotNull final NodePayloadIterableFactory nodePayloadIterableFactory) {
        this.nodePayloadIterableFactory = nodePayloadIterableFactory;
    }

    public <P> Observable<P> createFromNode(Node<P> node) {
        return Observable.from(nodePayloadIterableFactory.createForNode(node));
    }
}
