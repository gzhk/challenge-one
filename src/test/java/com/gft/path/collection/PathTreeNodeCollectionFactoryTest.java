package com.gft.path.collection;

import com.gft.path.PathTreeNode;
import org.junit.Test;

import javax.swing.tree.TreeNode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class PathTreeNodeCollectionFactoryTest {

    @Test
    public void createsCollectionFromPathTreeNode() throws Exception {
        List<TreeNode> children = new ArrayList<>();
        PathTreeNode rootNode = new PathTreeNode(mock(Path.class), children);

        List<TreeNode> firstChildChildren = new ArrayList<>();
        PathTreeNode firstChild = new PathTreeNode(mock(Path.class), rootNode, firstChildChildren);
        PathTreeNode firstChildOfFirstChild = new PathTreeNode(mock(Path.class), firstChild);
        firstChildChildren.add(firstChildOfFirstChild);
        PathTreeNode secondChildOfFirstChild = new PathTreeNode(mock(Path.class), firstChild);
        firstChildChildren.add(secondChildOfFirstChild);
        children.add(firstChild);

        PathTreeNode secondChild = new PathTreeNode(mock(Path.class), rootNode);
        children.add(secondChild);

        PathTreeNodeCollectionFactory factory = new PathTreeNodeCollectionFactory();

        PathTreeNodeCollection pathTreeNodeCollection = new PathTreeNodeCollection();
        pathTreeNodeCollection.add(rootNode);
        pathTreeNodeCollection.add(firstChild);
        pathTreeNodeCollection.add(firstChildOfFirstChild);
        pathTreeNodeCollection.add(secondChildOfFirstChild);
        pathTreeNodeCollection.add(secondChild);

        assertThat(factory.createFrom(rootNode), is(pathTreeNodeCollection));
    }
}
