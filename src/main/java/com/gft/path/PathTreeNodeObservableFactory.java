package com.gft.path;

import com.gft.path.collection.PathTreeNodeCollection;
import com.gft.path.collection.PathTreeNodeCollectionFactory;
import com.gft.path.treenode.CouldNotCreatePathTreeNode;
import com.gft.path.treenode.PathTreeNode;
import com.gft.path.treenode.PathTreeNodeFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.nio.file.Path;

public final class PathTreeNodeObservableFactory {

    private final PathTreeNodeFactory pathTreeNodeFactory;
    private final PathTreeNodeCollectionFactory pathTreeNodeCollectionFactory;

    public PathTreeNodeObservableFactory(
        @NotNull PathTreeNodeFactory pathTreeNodeFactory,
        @NotNull PathTreeNodeCollectionFactory pathTreeNodeCollectionFactory
    ) {
        this.pathTreeNodeFactory = pathTreeNodeFactory;
        this.pathTreeNodeCollectionFactory = pathTreeNodeCollectionFactory;
    }

    public Observable<PathTreeNode> createObservableForPath(@NotNull final Path path) throws CannotCreateObservable {
        PathTreeNode pathTreeNode;

        try {
            pathTreeNode = pathTreeNodeFactory.createFromPath(path);
        } catch (CouldNotCreatePathTreeNode e) {
            throw new CannotCreateObservable(path, e);
        }

        PathTreeNodeCollection collection = pathTreeNodeCollectionFactory.createFrom(pathTreeNode);

        return Observable.from(collection);
    }
}
