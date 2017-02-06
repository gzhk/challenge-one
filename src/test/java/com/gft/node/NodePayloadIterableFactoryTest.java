package com.gft.node;

import org.junit.Test;

public class NodePayloadIterableFactoryTest {

    @Test
    public void returnsIterableWithNodeIterator() throws Exception {
        NodePayloadIterableFactory nodePayloadIterableFactory = new NodePayloadIterableFactory();
        Iterable<String> iterable = nodePayloadIterableFactory.createForNode(new SimpleNode<>("root"));
    }
}
