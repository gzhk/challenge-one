package com.gft.path.collection;

import com.gft.path.PathTreeNode;
import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;

public final class PathTreeNodeCollectionFactory {

    public PathTreeNodeCollection createFrom(@NotNull PathTreeNode pathTreeNode) {
        PathTreeNodeCollection collection = new PathTreeNodeCollection();
        addNodesToCollection(pathTreeNode, collection);

        return collection;
    }

    private void addNodesToCollection(PathTreeNode pathTreeNode, PathTreeNodeCollection collection) {
        collection.add(pathTreeNode);

        Enumeration children = pathTreeNode.children();

        while (children.hasMoreElements()) {
            addNodesToCollection((PathTreeNode) children.nextElement(), collection);
        }
    }
}
