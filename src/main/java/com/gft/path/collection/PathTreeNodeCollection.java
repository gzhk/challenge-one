package com.gft.path.collection;

import com.gft.path.treenode.PathTreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class PathTreeNodeCollection implements Iterable<PathTreeNode> {

    private final List<PathTreeNode> paths = new ArrayList<>();

    public void add(PathTreeNode pathTreeNode) {
        paths.add(pathTreeNode);
    }

    @Override
    public Iterator<PathTreeNode> iterator() {
        if (paths.isEmpty()) {
            return new PathTreeNodeIterator();
        }

        return new PathTreeNodeIterator(paths.get(0), paths.get((paths.size() - 1)));
    }

    public PathTreeNode[] all() {
        Object[] array = paths.toArray();

        return Arrays.copyOf(array, array.length, PathTreeNode[].class);
    }

    @Override
    public int hashCode() {
        return paths.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PathTreeNodeCollection && paths.equals(((PathTreeNodeCollection) obj).paths);
    }
}
