package com.gft.path;

import com.gft.path.treenode.PathTreeNode;

import java.util.Iterator;

public class PathCollection implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        return new PathIterator();
    }
}
