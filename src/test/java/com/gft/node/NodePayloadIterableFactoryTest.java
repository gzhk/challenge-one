package com.gft.node;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NodePayloadIterableFactoryTest {

    @Test
    public void returnsIterableWithNodeIterator() throws Exception {
        NodePayloadIterableFactory nodePayloadIterableFactory = new NodePayloadIterableFactory();
        Iterable<String> iterable = nodePayloadIterableFactory.createForNode(new SimpleNode<>("root"));

        assertTrue(iterable.iterator() instanceof BreadthFirstSearchNodeIterator);
    }
}
