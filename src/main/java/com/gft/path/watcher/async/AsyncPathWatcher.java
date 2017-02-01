package com.gft.path.watcher.async;

import com.gft.path.treenode.PathTreeNode;
import com.gft.path.watcher.CouldNotRegisterPath;
import com.gft.path.watcher.NewPathsIterator;
import com.gft.path.watcher.PathWatcher;
import com.gft.path.watcher.PollWatchServiceEvents;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public final class AsyncPathWatcher implements PathWatcher {

    private final WatchService watchService;
    private final ExecutorService executorService;
    private final BlockingQueue<PathTreeNode> newPathsQueue = new LinkedBlockingQueue<>();

    public AsyncPathWatcher(@NotNull final WatchService watchService, @NotNull final ExecutorService executorService) {
        this.watchService = watchService;
        this.executorService = executorService;
    }

    @Override
    public void start(Path rootPath) {
        this.executorService.submit(new PollWatchServiceEvents(rootPath, watchService, newPathsQueue));
    }

    @Override
    public void registerPath(@NotNull final Path path) throws CouldNotRegisterPath {
        if (!Files.isDirectory(path)) {
            return;
        }

        try {
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            throw new CouldNotRegisterPath(path, e);
        }
    }

    @Override
    public void close() throws Exception {
        watchService.close();
        executorService.shutdown();
    }

    @Override
    public Iterator<PathTreeNode> iterator() {
        return new NewPathsIterator(newPathsQueue);
    }
}
