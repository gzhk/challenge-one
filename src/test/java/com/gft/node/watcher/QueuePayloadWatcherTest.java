package com.gft.node.watcher;

import org.junit.Test;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueuePayloadWatcherTest {

    @Test
    public void addsPayloadToTheQueue() throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        PayloadWatcher<String> payloadWatcher = new QueuePayloadWatcher<>(queue);

        payloadWatcher.call("payload");

        assertThat(queue, hasItem("payload"));
    }

    @Test
    public void addsPayloadToTheQueueUsingAction1Interface() throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        PayloadWatcher<String> payloadWatcher = new QueuePayloadWatcher<>(queue);

        payloadWatcher.call("payload");

        assertThat(queue, hasItem("payload"));
    }

    @Test
    public void returnsBlockingQueueIterator() throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        PayloadWatcher<String> payloadWatcher = new QueuePayloadWatcher<>(queue);
        payloadWatcher.call("payload");

        Iterator<String> iterator = payloadWatcher.iterator();

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is("payload"));
    }
}
