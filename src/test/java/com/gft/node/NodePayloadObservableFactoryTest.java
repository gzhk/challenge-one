package com.gft.node;

import org.junit.Test;
import rx.observables.ConnectableObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class NodePayloadObservableFactoryTest {

    @Test
    public void createsObservableWithPathsPayload() throws Exception {
        SimpleNode<String> rootLeaf1 = new SimpleNode<>("root_leaf1");
        SimpleNode<String> rootLeaf2 = new SimpleNode<>("root_leaf2");
        SimpleNode<String> rootNode = new SimpleNode<>("root", Arrays.asList(rootLeaf1, rootLeaf2));

        NodePayloadObservableFactory nodePayloadObservableFactory = new NodePayloadObservableFactory(new NodePayloadIterableFactory());
        ConnectableObservable<String> observable = nodePayloadObservableFactory.create(rootNode);

        List<String> emittedElements = new ArrayList<>();
        observable.subscribe(emittedElements::add);
        observable.connect();

        assertThat(emittedElements, hasItems("root", "root_leaf1", "root_leaf2"));
    }
}
