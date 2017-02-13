package com.gft.path;

import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class WatchServicePayloadRegistryFactoryTest {

    @Test(expected = CouldNotCreateWatchServicePayloadRegistry.class)
    public void throwsExceptionWhenCannotCreateNewWatchService() throws Exception {
        FileSystem fileSystem = mock(FileSystem.class);
        doThrow(IOException.class).when(fileSystem).newWatchService();

        new WatchServicePayloadRegistryFactory(fileSystem).create();
    }

    @Test
    public void createsNewInstanceOfWatchServicePayloadRegistry() throws Exception {
        WatchServicePayloadRegistryFactory factory = new WatchServicePayloadRegistryFactory(Jimfs.newFileSystem());

        WatchServicePayloadRegistry watchServicePayloadRegistry = factory.create();

        assertThat(watchServicePayloadRegistry, is(notNullValue()));
        assertFalse(watchServicePayloadRegistry.equals(factory.create()));
    }
}
