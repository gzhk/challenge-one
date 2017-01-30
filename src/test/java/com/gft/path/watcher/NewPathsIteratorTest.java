package com.gft.path.watcher;

import com.gft.path.treenode.PathTreeNode;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewPathsIteratorTest {

    @Test
    public void itIsIterator() throws Exception {
        assertThat(new NewPathsIterator(new ArrayBlockingQueue<>(16)), is(instanceOf(Iterator.class)));
    }

    @Test(expected = RuntimeException.class)
    public void wrapsInterruptionException() throws Exception {
        ArrayBlockingQueue pathTreeNodes = mock(ArrayBlockingQueue.class);
        NewPathsIterator newPathsIterator = new NewPathsIterator(pathTreeNodes);

        when(pathTreeNodes.take()).thenThrow(InterruptedException.class);

        newPathsIterator.next();
    }

    @Test
    public void returnsElementsFromQueue() throws Exception {
        ArrayBlockingQueue<PathTreeNode> pathTreeNodes = new ArrayBlockingQueue<>(16);
        NewPathsIterator newPathsIterator = new NewPathsIterator(pathTreeNodes);
        PathTreeNode firstPathTreeNode = new PathTreeNode(mock(Path.class));
        PathTreeNode secondPathTreeNode = new PathTreeNode(mock(Path.class));

        pathTreeNodes.add(firstPathTreeNode);
        pathTreeNodes.add(secondPathTreeNode);

        assertThat(newPathsIterator.next(), is(firstPathTreeNode));
        assertThat(newPathsIterator.next(), is(secondPathTreeNode));
    }

    @Test
    public void itReturnsTrue() throws Exception {
        NewPathsIterator newPathsIterator = new NewPathsIterator(new ArrayBlockingQueue<PathTreeNode>(16));
        assertThat(newPathsIterator.hasNext(), is(true));
    }
}
