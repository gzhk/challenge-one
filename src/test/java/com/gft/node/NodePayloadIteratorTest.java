package com.gft.node;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Arrays;

public class NodePayloadIteratorTest {

    @Test
    public void returnsPayloadInCorrectOrder() throws Exception {
        SimpleNode<String> rootLeaf1 = new SimpleNode<>("root_leaf1");
        SimpleNode<String> rootLeaf2 = new SimpleNode<>("root_leaf2");
        SimpleNode<String> subRootNodeLeaf = new SimpleNode<>("sub_root_node_leaf");
        SimpleNode<String> subRootNode = new SimpleNode<>("sub_root_node", Arrays.asList(subRootNodeLeaf));
        SimpleNode<String> rootNode = new SimpleNode<>("root", Arrays.asList(subRootNode, rootLeaf1, rootLeaf2));

        Assertions
            .assertThat(new NodePayloadIterator<>(rootNode))
            .containsOnly("root", "sub_root_node", "root_leaf1", "root_leaf2", "sub_root_node_leaf");
    }
}
