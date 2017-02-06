package com.gft.path.watcher;

import com.gft.path.treenode.PathTreeNode;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

public final class NewPathsIterator implements Iterator<PathTreeNode> {

    private final BlockingQueue<PathTreeNode> pathTreeNodes;

    public NewPathsIterator(@NotNull final BlockingQueue<PathTreeNode> pathTreeNodes) {
        this.pathTreeNodes = pathTreeNodes;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public PathTreeNode next() {
        try {
            return pathTreeNodes.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not return next element, because thread has been interrupted.", e);
        }
    }
}
