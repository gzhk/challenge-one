package com.gft.watchservice;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RegistersPathsTest {

    @Test(timeout = 10000)
    public void registersPathsReturnedByDecoratedIteratorInWatchService() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        WatchServicePaths watchServicePaths = () -> Collections.singletonList(rootPath);
        WatchService watchService = fileSystem.newWatchService();

        RegistersPaths registersPaths = new RegistersPaths(watchServicePaths, watchService);
        List<Path> paths = registersPaths.poll();

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
        Assertions.assertThat(paths).containsOnly(rootPath);
    }
}
