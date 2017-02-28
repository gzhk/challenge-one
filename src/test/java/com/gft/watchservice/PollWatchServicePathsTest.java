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

public final class PollWatchServicePathsTest {

    @Test(timeout = 10000)
    public void returnsPolledPaths() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);
        rootPath.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);

        PollWatchServicePaths pollWatchServicePaths = new PollWatchServicePaths(watchService);

        Path multiLevelPath = rootPath.resolve("level1/level2/level3");
        Files.createDirectories(multiLevelPath);

        Path file = rootPath.resolve("file.txt");
        Files.write(file, Collections.singleton("line"), Charset.forName("UTF-8"));

        final List<Path> returned = new ArrayList<>();

        while (returned.size() < 4) {
            returned.addAll(pollWatchServicePaths.poll());
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
    public void returnsEmptyListIfThereAreNoPaths() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);
        rootPath.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);

        PollWatchServicePaths pollWatchServicePaths = new PollWatchServicePaths(watchService);

        Assertions.assertThat(pollWatchServicePaths.poll()).isEmpty();
    }
}
