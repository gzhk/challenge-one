package com.gft.watchservice;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.sun.nio.file.SensitivityWatchEventModifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RegistersPathsTest {

    @Test(timeout = 10000)
    public void registersPathsReturnedByPollWatchServicePaths() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        WatchService watchService = fileSystem.newWatchService();
        RegistersPaths.register(Stream.of(rootPath), watchService);

        Path subPath = rootPath.resolve("subPath");
        Files.createDirectory(subPath);

        WatchKey watchKey = watchService.take();
        List<Path> collect = watchKey
            .pollEvents()
            .stream()
            .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
            .map(p -> ((Path) watchKey.watchable()).resolve(p))
            .collect(Collectors.toList());

        Assertions.assertThat(collect).contains(subPath);
    }
}
