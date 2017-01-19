package com.gft.path.collection;

import com.gft.path.PathTreeNode;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class PathTreeNodeCollectionTest {

    @Test
    public void addsPathThreeNodeToCollection() throws Exception {
        PathTreeNodeCollection pathTreeNodes = new PathTreeNodeCollection();
        PathTreeNode firstPathTreeNode = new PathTreeNode(mock(Path.class));
        PathTreeNode secondPathTreeNode = new PathTreeNode(mock(Path.class));
        pathTreeNodes.add(firstPathTreeNode);
        pathTreeNodes.add(secondPathTreeNode);

        assertThat(pathTreeNodes.all(), is(new PathTreeNode[]{firstPathTreeNode, secondPathTreeNode}));
    }

    @Test
    public void returnsPathThreeNodeIterator() throws Exception {
        PathTreeNodeCollection pathTreeNodes = new PathTreeNodeCollection();
        assertThat(pathTreeNodes.iterator(), is(instanceOf(PathTreeNodeIterator.class)));
    }
}
