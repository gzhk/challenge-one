package com.gft.watchservice;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.sun.nio.file.SensitivityWatchEventModifier;
import edu.emory.mathcs.backport.java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class WatchServiceFunctionsTest {

    @Test(timeout = 10000)
    public void registersPathsReturnedByPollWatchServicePaths() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        WatchService watchService = fileSystem.newWatchService();
        WatchServiceFunctions.registerPaths(Stream.of(rootPath), watchService);

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

    @Test(timeout = 10000)
    public void returnsStreamWithPolledPaths() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);
        rootPath.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);


        Path multiLevelPath = rootPath.resolve("level1/level2/level3");
        Files.createDirectories(multiLevelPath);

        Path file = rootPath.resolve("file.txt");
        Files.write(file, Collections.singleton("line"), Charset.forName("UTF-8"));

        final List<Path> returned = new ArrayList<>();

        while (returned.size() < 4) {
            returned.addAll(WatchServiceFunctions.pollPaths(watchService));
        }

        Assertions
            .assertThat(returned)
            .containsOnly(
                rootPath.resolve("level1"),
                rootPath.resolve("level1/level2"),
                rootPath.resolve("level1/level2/level3"),
                rootPath.resolve("file.txt")
            );
    }

    @Test
    public void returnsEmptyStreamIfThereAreNoPaths() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);
        rootPath.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);

        Assertions.assertThat(WatchServiceFunctions.pollPaths(watchService)).isEmpty();
    }
}
