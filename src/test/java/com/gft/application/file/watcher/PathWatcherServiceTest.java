package com.gft.application.file.watcher;

import com.gft.node.NodePayloadIterableFactory;
import com.gft.node.NodePayloadObservableFactory;
import com.gft.path.PathNode;
import com.gft.path.WatchServicePayloadRegistryFactory;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import rx.Observer;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class PathWatcherServiceTest {

    @Test(timeout = 3000)
    public void sendsItemsFromStreamThroughWebSockets() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path path = fileSystem.getPath("/tmp");
        Files.createDirectories(path);

        PathNode pathNode = new PathNode(path);
        NodePayloadObservableFactory nodePayloadObservableFactory = new NodePayloadObservableFactory(new NodePayloadIterableFactory());
        WatchServicePayloadRegistryFactory payloadRegistryFactory = new WatchServicePayloadRegistryFactory(fileSystem);
        PathWatcherService pathWatcherService = new PathWatcherService(payloadRegistryFactory, nodePayloadObservableFactory);

        ConcurrentLinkedQueue<Object> emittedPaths = new ConcurrentLinkedQueue<>();

        pathWatcherService.watch(pathNode, new Observer<Path>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(final Throwable e) {
            }

            @Override
            public void onNext(final Path path) {
                emittedPaths.add(path);
            }
        });

        while (emittedPaths.size() < 1) {
            // wait for paths to appear otherwise timeout will fail this test.
        }

        assertThat(emittedPaths, hasItem(path));
    }
}
