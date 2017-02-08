package com.gft.node;

import com.gft.node.watcher.PayloadWatcher;
import com.gft.node.watcher.QueuePayloadWatcher;
import org.junit.Test;
import rx.observables.ConnectableObservable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NodePayloadIteratorObservableFactoryTest {

    @Test
    public void createsObservableWithPathsPayload() throws Exception {
        SimpleNode<String> rootNode = new SimpleNode<>("root");
        SimpleNode<String> rootLeaf1 = new SimpleNode<>("root_leaf1");
        SimpleNode<String> rootLeaf2 = new SimpleNode<>("root_leaf2");

        rootNode.addChild(rootLeaf1);
        rootNode.addChild(rootLeaf2);

        NodePayloadObservableFactory nodePayloadObservableFactory = new NodePayloadIteratorObservableFactory(new NodePayloadIterableFactory());
        ConnectableObservable<String> observable = nodePayloadObservableFactory.createForNode(rootNode);

        List<String> emittedElements = new ArrayList<>();
        observable.subscribe(emittedElements::add);
        observable.connect();

        assertThat(emittedElements, hasItems("root", "root_leaf1", "root_leaf2"));
    }

    @Test
    public void createsObservableWhichEmitsChangesFromPayloadWatcher() throws Exception {
        SimpleNode<String> rootNode = new SimpleNode<>("root");

        NodePayloadObservableFactory nodePayloadObservableFactory = new NodePayloadIteratorObservableFactory(new NodePayloadIterableFactory());

        PayloadWatcher<String> payloadWatcher = new QueuePayloadWatcher<>(new LinkedBlockingQueue<>());
        ConnectableObservable<String> observable = nodePayloadObservableFactory.createWithWatcher(rootNode, payloadWatcher);

        List<String> emittedElements = new ArrayList<>();
        observable.limit(2).subscribe(emittedElements::add);
        observable.connect();

        assertThat(emittedElements.size(), is(2));
        assertThat(emittedElements, hasItems("root", "root"));
    }

    @Test
    public void wrapsExceptionFromRegisterPayloadMethod() throws Exception {

    }
}
