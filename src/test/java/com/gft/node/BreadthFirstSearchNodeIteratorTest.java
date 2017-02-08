package com.gft.node;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BreadthFirstSearchNodeIteratorTest {

    @Test
    public void returnsPayloadInCorrectOrder() throws Exception {
        SimpleNode<String> rootNode = new SimpleNode<>("root");
        SimpleNode<String> subRootNode = new SimpleNode<>("sub_root_node");
        SimpleNode<String> rootLeaf1 = new SimpleNode<>("root_leaf1");
        SimpleNode<String> rootLeaf2 = new SimpleNode<>("root_leaf2");
        SimpleNode<String> subRootNodeLeaf = new SimpleNode<>("sub_root_node_leaf");

        rootNode.addChild(subRootNode);
        rootNode.addChild(rootLeaf1);
        rootNode.addChild(rootLeaf2);
        subRootNode.addChild(subRootNodeLeaf);

        BreadthFirstSearchNodeIterator<String> iterator = new BreadthFirstSearchNodeIterator<>(rootNode);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is("root"));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is("sub_root_node"));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is("root_leaf1"));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is("root_leaf2"));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is("sub_root_node_leaf"));

        assertThat(iterator.hasNext(), is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void throwsNoSuchElementExceptionIfThereIsNoNextValue() throws Exception {
        BreadthFirstSearchNodeIterator<String> iterator = new BreadthFirstSearchNodeIterator<>(new SimpleNode<>("root"));

        assertThat(iterator.hasNext(), is(true));
        iterator.next();

        assertThat(iterator.hasNext(), is(false));
        iterator.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void wrapsCannotRetrieveChildrenException() throws Exception {
        Node node = mock(Node.class);
        when(node.children()).thenThrow(CannotRetrieveChildren.class);

        BreadthFirstSearchNodeIterator<Object> objectBreadthFirstSearchNodeIterator = new BreadthFirstSearchNodeIterator<>(node);
        objectBreadthFirstSearchNodeIterator.next();
    }
}
