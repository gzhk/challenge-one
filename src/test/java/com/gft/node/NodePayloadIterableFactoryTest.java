package com.gft.node;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class NodePayloadIterableFactoryTest {

    @Test
    public void returnsIterableWithNodeIterator() throws Exception {
        NodePayloadIterableFactory nodePayloadIterableFactory = new NodePayloadIterableFactory();
        Iterable<String> iterable = nodePayloadIterableFactory.createForNode(new SimpleNode<>("root"));
    }
}
