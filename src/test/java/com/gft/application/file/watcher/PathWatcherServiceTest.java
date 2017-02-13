package com.gft.application.file.watcher;

import com.gft.node.NodePayloadObservableFactory;
import com.gft.node.watcher.PayloadRegistry;
import com.gft.path.PathNode;
import com.gft.path.WatchServicePayloadRegistryFactory;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import rx.Observer;
import rx.observables.ConnectableObservable;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PathWatcherServiceTest {

    @Test(timeout = 3000)
    public void sendsItemsFromStreamThroughWebSockets() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path path = fileSystem.getPath("/tmp");
        Files.createDirectories(path);

        PathNode pathNode = new PathNode(path);
        NodePayloadObservableFactory nodePayloadObservableFactory = mock(NodePayloadObservableFactory.class);
        ConnectableObservable<Path> connectableObservable = ConnectableObservable.from(Collections.singleton(path)).publish();

        WatchServicePayloadRegistryFactory payloadRegistryFactory = new WatchServicePayloadRegistryFactory(fileSystem);
        when(nodePayloadObservableFactory.createWithWatcher(eq(pathNode), any(PayloadRegistry.class))).thenReturn(connectableObservable);
        PathWatcherService pathWatcherService = new PathWatcherService(payloadRegistryFactory, nodePayloadObservableFactory);

        ArrayList<Path> emittedPaths = new ArrayList<>();

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
