package com.gft.node;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class SimpleNodeTest {

    @Test
    public void returnsPayload() throws Exception {
        SimpleNode<String> simpleNode = new SimpleNode<>("root");
        assertThat(simpleNode.getPayload(), is("root"));
    }

    @Test
    public void addsAndReturnsChildren() throws Exception {
        SimpleNode<String> child = new SimpleNode<>("child");
        SimpleNode<String> simpleNode = new SimpleNode<>("root", Collections.singletonList(child));

        assertThat(simpleNode.children(), is(Collections.singletonList(child)));
    }

    @Test
    public void returnsTrueIfPayloadIsEqual() throws Exception {
        assertThat(new SimpleNode<>("root", new ArrayList<>()), is(new SimpleNode<>("root")));
    }

    @Test
    public void returnsFalseIfPayloadIsNotEqual() throws Exception {
        assertThat(new SimpleNode<>("root"), is(not(new SimpleNode<>("different root"))));
    }

    @Test
    public void computesHashCodeBasedOnPayload() throws Exception {
        assertThat(new SimpleNode<>("root").hashCode(), is(Objects.hash("root")));
    }
}
