package com.gft.application.path;

import com.gft.node.NodePayloadIterableFactory;
import com.gft.node.SimpleNode;
import org.junit.Test;
import rx.Observable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PathPayloadObservableFactoryTest {

    @Test
    public void createsObservableWithPathsPayload() throws Exception {
        SimpleNode<String> rootNode = new SimpleNode<>("root");
        SimpleNode<String> rootLeaf1 = new SimpleNode<>("root_leaf1");
        SimpleNode<String> rootLeaf2 = new SimpleNode<>("root_leaf2");

        rootNode.addChild(rootLeaf1);
        rootNode.addChild(rootLeaf2);

        PathPayloadObservableFactory pathPayloadObservableFactory = new PathPayloadObservableFactory(new NodePayloadIterableFactory());
        Observable<String> observable = pathPayloadObservableFactory.createFromNode(rootNode);

        observable.elementAt(0).subscribe(s -> assertThat(s, is("root")));
        observable.elementAt(1).subscribe(s -> assertThat(s, is("root_leaf1")));
        observable.elementAt(2).subscribe(s -> assertThat(s, is("root_leaf2")));
    }
}
