package com.gft.application.file.watcher;

import com.gft.node.NodePayloadIterableFactory;
import com.gft.node.NodePayloadObservableFactory;
import com.gft.path.PathNode;
import com.gft.path.WatchServicePayloadRegistryFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import rx.Observer;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public final class PathWatcherIntegrationTest {

    @Test(timeout = 10000)
    public void emitsPathsAndRegisteredChanges() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        NodePayloadObservableFactory nodePayloadObservableFactory = new NodePayloadObservableFactory(
            new NodePayloadIterableFactory()
        );
        WatchServicePayloadRegistryFactory payloadRegistryFactory = new WatchServicePayloadRegistryFactory(fileSystem);
        PathWatcherService pathWatcherService = new PathWatcherService(payloadRegistryFactory, nodePayloadObservableFactory);

        Path root = fileSystem.getPath("/tmp");
        Files.createDirectories(root);

        Path firstLevelSubDir = root.resolve("firstLevelSubDir");
        Files.createDirectories(firstLevelSubDir);

        Path secondLevelSubDir = firstLevelSubDir.resolve("secondLevelSubDir");
        Files.createDirectories(secondLevelSubDir);

        ConcurrentLinkedQueue<Path> paths = new ConcurrentLinkedQueue<>();

        pathWatcherService.watch(new PathNode(root), new Observer<Path>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(final Throwable e) {

            }

            @Override
            public void onNext(final Path path) {
                paths.add(path);
            }
        });

        while (paths.size() < 3) {
            // wait until existing directories are emitted
        }

        Path rootFile = root.resolve("rootFile.txt");
        Files.write(rootFile, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

        Path firstLevelDir2 = firstLevelSubDir.resolve("firstLevelDir2");
        Files.createDirectories(firstLevelDir2);

        Path secondLevelFile = secondLevelSubDir.resolve("secondLevelFile.txt");
        Files.write(secondLevelFile, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

        Path veryNewDir = root.resolve("a/b/c/d");
        Files.createDirectories(veryNewDir);

        while (paths.size() < 7) {
            // wait until added directories/files are emitted
        }

        assertThat(
            paths,
            hasItems(
                root,
                firstLevelSubDir,
                secondLevelSubDir,
                rootFile,
                firstLevelDir2,
                secondLevelFile,
                veryNewDir
            )
        );
    }
}
