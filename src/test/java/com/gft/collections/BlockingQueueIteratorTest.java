package com.gft.collections;

import com.gft.collections.BlockingQueueIterator;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlockingQueueIteratorTest {

    @Test
    public void itIsIterator() throws Exception {
        assertThat(new BlockingQueueIterator<Path>(new ArrayBlockingQueue<>(16)), is(instanceOf(Iterator.class)));
    }

    @Test(expected = RuntimeException.class)
    public void wrapsInterruptionException() throws Exception {
        ArrayBlockingQueue<Path> paths = mock(ArrayBlockingQueue.class);
        BlockingQueueIterator<Path> blockingQueueIterator = new BlockingQueueIterator<>(paths);

        when(paths.take()).thenThrow(InterruptedException.class);

        blockingQueueIterator.next();
    }

    @Test
    public void returnsElementsFromQueue() throws Exception {
        ArrayBlockingQueue<Path> paths = new ArrayBlockingQueue<>(16);
        BlockingQueueIterator<Path> blockingQueueIterator = new BlockingQueueIterator<>(paths);
        Path firstPath = mock(Path.class);
        Path secondPath = mock(Path.class);

        paths.add(firstPath);
        paths.add(secondPath);

        assertThat(blockingQueueIterator.next(), is(firstPath));
        assertThat(blockingQueueIterator.next(), is(secondPath));
    }

    @Test
    public void itReturnsTrue() throws Exception {
        BlockingQueueIterator<Path> blockingQueueIterator = new BlockingQueueIterator<>(new ArrayBlockingQueue<>(16));
        assertThat(blockingQueueIterator.hasNext(), is(true));
    }
}
