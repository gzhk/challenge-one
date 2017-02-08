package com.gft.application.file.watcher;

import com.gft.node.NodePayloadObservableFactory;
import com.gft.node.watcher.PayloadWatcher;
import com.gft.path.PathNode;
import com.gft.path.watcher.async.AsyncPathWatcher;
import com.gft.path.watcher.async.AsyncPathWatcherFactory;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import rx.Observable;
import rx.observables.ConnectableObservable;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PathWatcherServiceTest {

    @Test(expected = PathWatcherServiceFailed.class)
    public void wrapsExceptionWhenTryingToCloseAsyncPathWatcher() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path path = fileSystem.getPath("/tmp");
        Files.createDirectories(path);

        PathNode pathNode = new PathNode(path);
        NodePayloadObservableFactory nodePayloadObservableFactory = mock(NodePayloadObservableFactory.class);
        ConnectableObservable connectableObservable = mock(ConnectableObservable.class);

        AsyncPathWatcherFactory asyncPathWatcherFactory = mock(AsyncPathWatcherFactory.class);
        AsyncPathWatcher asyncPathWatcher = mock(AsyncPathWatcher.class);
        when(asyncPathWatcherFactory.create()).thenReturn(asyncPathWatcher);
        doThrow(Exception.class).when(asyncPathWatcher).close();

        when(nodePayloadObservableFactory.createWithWatcher(eq(pathNode), any(PayloadWatcher.class))).thenReturn(connectableObservable);
        PathWatcherService pathWatcherService = new PathWatcherService(asyncPathWatcherFactory, nodePayloadObservableFactory);

        pathWatcherService.watch(pathNode, o -> {});
    }

    @Test
    public void sendsItemsFromStreamThroughWebSockets() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path path = fileSystem.getPath("/tmp");
        Files.createDirectories(path);

        PathNode pathNode = new PathNode(path);
        NodePayloadObservableFactory nodePayloadObservableFactory = mock(NodePayloadObservableFactory.class);
        ConnectableObservable<Path> connectableObservable = ConnectableObservable.from(Collections.singleton(path)).publish();

        AsyncPathWatcherFactory asyncPathWatcherFactory = new AsyncPathWatcherFactory(fileSystem);
        when(nodePayloadObservableFactory.createWithWatcher(eq(pathNode), any(PayloadWatcher.class))).thenReturn(connectableObservable);
        PathWatcherService pathWatcherService = new PathWatcherService(asyncPathWatcherFactory, nodePayloadObservableFactory);

        ArrayList<Path> emittedPaths = new ArrayList<>();

        pathWatcherService.watch(pathNode, emittedPaths::add);

        assertThat(emittedPaths, hasItem(path));
    }
}
