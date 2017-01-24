package com.gft.application.file.list;

import com.gft.path.collection.PathTreeNodeCollection;
import com.gft.path.collection.PathTreeNodeCollectionFactory;
import com.gft.path.treenode.PathTreeNode;
import com.gft.path.treenode.PathTreeNodeFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.nio.file.Path;

@Service
public final class ListService {

    private final PathTreeNodeFactory pathTreeNodeFactory;
    private final PathTreeNodeCollectionFactory pathTreeNodeCollectionFactory;

    public ListService(
        @NotNull PathTreeNodeFactory pathTreeNodeFactory,
        @NotNull PathTreeNodeCollectionFactory pathTreeNodeCollectionFactory
    ) {
        this.pathTreeNodeFactory = pathTreeNodeFactory;
        this.pathTreeNodeCollectionFactory = pathTreeNodeCollectionFactory;
    }

    public Observable<PathTreeNode> createObservableForPath(@NotNull final Path path) {
        PathTreeNode pathTreeNode = pathTreeNodeFactory.createFromPath(path);
        PathTreeNodeCollection collection = pathTreeNodeCollectionFactory.createFrom(pathTreeNode);

        return Observable.from(collection);
    }
}
