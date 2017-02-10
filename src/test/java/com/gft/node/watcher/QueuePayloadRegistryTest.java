package com.gft.node.watcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class QueuePayloadRegistryTest {

    @Test
    public void addsPayloadToTheQueue() throws Exception {
        List<String> list = new ArrayList<>();
        PayloadRegistry<String> payloadRegistry = new QueuePayloadRegistry<>(list);

        payloadRegistry.registerPayload("payload");

        assertThat(list, hasItem("payload"));
    }

    @Test
    public void returnsChangesObservableWithRegisteredChanges() throws Exception {
        List<String> list = new ArrayList<>();
        PayloadRegistry<String> payloadRegistry = new QueuePayloadRegistry<>(list);

        payloadRegistry.registerPayload("payload");
        payloadRegistry.registerPayload("payload2");

        ArrayList<String> emittedStrings = new ArrayList<>();

        payloadRegistry.changes().subscribe(emittedStrings::add);

        assertThat(emittedStrings, hasItems("payload", "payload2"));
    }
}
