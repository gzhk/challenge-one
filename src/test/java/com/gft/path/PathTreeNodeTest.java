package com.gft.path;

import org.junit.Test;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathTreeNodeTest {

    @Test
    public void returnsTrueIfDoesNotHaveChildren() throws Exception {
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class));

        assertTrue(pathTreeNode.isLeaf());
    }

    @Test
    public void returnsFalseIfHaveChildren() throws Exception {
        PathTreeNode child = new PathTreeNode(mock(Path.class));
        List<TreeNode> children = new ArrayList<>();
        children.add(child);
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class), children);

        assertFalse(pathTreeNode.isLeaf());
    }

    @Test
    public void returnsParent() throws Exception {
        PathTreeNode parent = new PathTreeNode(mock(Path.class));
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class), parent);

        assertEquals(parent, pathTreeNode.getParent());
    }

    @Test
    public void returnsNullIfDoesNotHaveParent() throws Exception {
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class));

        assertNull(pathTreeNode.getParent());
    }

    @Test
    public void returnsChildByIndex() throws Exception {
        PathTreeNode child = new PathTreeNode(mock(Path.class));
        List<TreeNode> children = new ArrayList<>();
        children.add(child);
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class), children);

        assertEquals(child, pathTreeNode.getChildAt(0));
    }

    @Test
    public void returnsChildrenCount() throws Exception {
        PathTreeNode child = new PathTreeNode(mock(Path.class));
        List<TreeNode> children = new ArrayList<>();
        children.add(child);
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class), children);

        assertEquals(1, pathTreeNode.getChildCount());
    }

    @Test
    public void returnsIndexOfGivenTreeNode() throws Exception {
        PathTreeNode firstChild = new PathTreeNode(mock(Path.class));
        PathTreeNode secondChild = new PathTreeNode(mock(Path.class));
        List<TreeNode> children = new ArrayList<>();
        children.add(firstChild);
        children.add(secondChild);
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class), children);

        assertEquals(0, pathTreeNode.getIndex(firstChild));
        assertEquals(1, pathTreeNode.getIndex(secondChild));
    }

    @Test
    public void returnsTrueIfPathIsADirectory() throws Exception {
        File file = mock(File.class);
        Path path = mock(Path.class);
        PathTreeNode pathTreeNode = new PathTreeNode(path);

        when(path.toFile()).thenReturn(file);
        when(file.isDirectory()).thenReturn(true);

        assertTrue(pathTreeNode.getAllowsChildren());
    }

    @Test
    public void returnsFalseIfPathIsAFile() throws Exception {
        File file = mock(File.class);
        Path path = mock(Path.class);
        PathTreeNode pathTreeNode = new PathTreeNode(path);

        when(path.toFile()).thenReturn(file);
        when(file.isDirectory()).thenReturn(false);

        assertFalse(pathTreeNode.getAllowsChildren());
    }

    @Test
    public void returnsEnumerationWithChildren() throws Exception {
        PathTreeNode firstChild = new PathTreeNode(mock(Path.class));
        PathTreeNode secondChild = new PathTreeNode(mock(Path.class));
        List<TreeNode> children = new ArrayList<>();
        children.add(firstChild);
        children.add(secondChild);
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class), children);

        Enumeration enumeration = pathTreeNode.children();
        assertEquals(firstChild, enumeration.nextElement());
        assertEquals(secondChild, enumeration.nextElement());
    }

    @Test
    public void itCanHaveRootAndChildren() throws Exception {
        PathTreeNode root = new PathTreeNode(mock(Path.class));
        PathTreeNode firstChild = new PathTreeNode(mock(Path.class));
        List<TreeNode> children = new ArrayList<>();
        children.add(firstChild);
        PathTreeNode pathTreeNode = new PathTreeNode(mock(Path.class), root, children);

        assertThat(pathTreeNode.getParent(), is(root));
        assertThat(pathTreeNode.getChildCount(), is(1));
    }

    @Test
    public void itReturnsPath() throws Exception {
        Path nodePath = mock(Path.class);
        PathTreeNode pathTreeNode = new PathTreeNode(nodePath);

        assertThat(pathTreeNode.path(), is(nodePath));
    }

    @Test
    public void returnsHashCodeFromPath() throws Exception {
        Path nodePath = mock(Path.class);
        PathTreeNode pathTreeNode = new PathTreeNode(nodePath);

        assertThat(pathTreeNode.hashCode(), is(nodePath.hashCode()));
    }

    @Test
    public void equalsReturnsTrueIfNodePathsAreEqual() throws Exception {
        Path nodePath = mock(Path.class);
        PathTreeNode pathTreeNode = new PathTreeNode(nodePath);
        PathTreeNode equalPathTreeNode = new PathTreeNode(nodePath);

        assertTrue(pathTreeNode.equals(equalPathTreeNode));
    }
}
