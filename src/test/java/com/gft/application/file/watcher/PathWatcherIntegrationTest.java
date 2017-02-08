package com.gft.application.file.watcher;

import com.gft.node.NodePayloadIterableFactory;
import com.gft.node.NodePayloadIteratorObservableFactory;
import com.gft.path.PathNode;
import com.gft.path.watcher.async.AsyncPathWatcherFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PathWatcherIntegrationTest {

//    @Test(timeout = 2000)
    public void emitsPathsAndRegisteredChanges() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        NodePayloadIteratorObservableFactory nodePayloadObservableFactory = new NodePayloadIteratorObservableFactory(
            new NodePayloadIterableFactory()
        );
        AsyncPathWatcherFactory asyncPathWatcherFactory = new AsyncPathWatcherFactory(fileSystem);
        PathWatcherService pathWatcherService = new PathWatcherService(asyncPathWatcherFactory, nodePayloadObservableFactory);

        Path root = fileSystem.getPath("/tmp");
        Path rootFile = root.resolve("rootFile.txt");
        Path firstLevelSubDir = root.resolve("firstLevelSubDir");
        Path firstLevelFile = firstLevelSubDir.resolve("firstLevelFile.txt");
        Path secondLevelSubDir = firstLevelSubDir.resolve("secondLevelSubDir");
        Path secondLevelFile = secondLevelSubDir.resolve("secondLevelFile.txt");

        Files.createDirectories(root);
        Files.createDirectories(firstLevelSubDir);
        Files.createDirectories(secondLevelSubDir);
        Files.write(rootFile, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
        Files.write(secondLevelFile, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
        Files.write(firstLevelFile, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

        pathWatcherService.watch(new PathNode(root), System.out::println);
    }
}
