package com.gft.path;

import com.gft.path.treenode.PathTreeNode;

import java.util.Iterator;

public class PathIterator implements Iterator<String> {

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String next() {
        try {
            Thread.sleep(1000);
            return "Hey hey";
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
