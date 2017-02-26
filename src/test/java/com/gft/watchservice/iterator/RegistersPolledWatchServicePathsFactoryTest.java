package com.gft.watchservice.iterator;

import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.nio.file.WatchService;

public final class RegistersPolledWatchServicePathsFactoryTest {

    @Test
    public void returnsRegisterPathsIterator() throws Exception {
        WatchService watchService = Jimfs.newFileSystem().newWatchService();
        RegistersPolledWatchServicePathsFactory factory = new RegistersPolledWatchServicePathsFactory();
        Assertions.assertThat(factory.create(watchService)).isInstanceOf(RegistersPaths.class);
    }
}
