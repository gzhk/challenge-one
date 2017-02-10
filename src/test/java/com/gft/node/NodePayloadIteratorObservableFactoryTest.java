package com.gft.node;

import org.junit.Test;
import rx.observables.ConnectableObservable;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class NodePayloadIteratorObservableFactoryTest {

    @Test(timeout = 1000)
    public void createsObservableWithPathsPayload() throws Exception {
        SimpleNode<String> rootNode = new SimpleNode<>("root");
        SimpleNode<String> rootLeaf1 = new SimpleNode<>("root_leaf1");
        SimpleNode<String> rootLeaf2 = new SimpleNode<>("root_leaf2");

        rootNode.addChild(rootLeaf1);
        rootNode.addChild(rootLeaf2);

        NodePayloadObservableFactory nodePayloadObservableFactory = new NodePayloadIteratorObservableFactory(new NodePayloadIterableFactory());
        ConnectableObservable<String> observable = nodePayloadObservableFactory.createForNode(rootNode);

        ConcurrentLinkedQueue<String> emittedElements = new ConcurrentLinkedQueue<>();
        observable.subscribe(emittedElements::add);
        observable.connect();

        while (emittedElements.size() < 3) {
            // wait for payloads to appear
        }

        assertThat(emittedElements, hasItems("root", "root_leaf1", "root_leaf2"));
    }

    @Test(timeout = 1000)
    public void createsObservableWhichEmitsChangesFromPayloadWatcher() throws Exception {
//        SimpleNode<String> rootNode = new SimpleNode<>("root");
//
//        NodePayloadObservableFactory nodePayloadObservableFactory = new NodePayloadIteratorObservableFactory(new NodePayloadIterableFactory());
//
//        PayloadWatcher<String> payloadWatcher = new QueuePayloadWatcher<>(new LinkedBlockingQueue<>());
//        ConnectableObservable<String> observable = nodePayloadObservableFactory.createWithWatcher(rootNode, payloadWatcher);
//
//        ConcurrentLinkedQueue<String> emittedElements = new ConcurrentLinkedQueue<>();
//        observable.limit(2).subscribe(emittedElements::add);
//        observable.connect();
//
//        while (emittedElements.size() < 2) {
//            // wait for payloads to appear
//        }
//
//        assertThat(emittedElements, hasItems("root", "root"));
    }
}
