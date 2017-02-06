package com.gft.path;

import com.gft.path.collection.PathTreeNodeCollection;
import com.gft.path.collection.PathTreeNodeCollectionFactory;
import com.gft.path.treenode.CouldNotCreatePathTreeNode;
import com.gft.path.treenode.PathTreeNode;
import com.gft.path.treenode.PathTreeNodeFactory;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathTreeNodeObservableFactoryTest {

    @Test
    public void createsObservableFromPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fileSystem.getPath("C:\\root");
        Files.createDirectory(rootPath);

        PathTreeNodeCollection pathTreeNodeCollection = new PathTreeNodeCollection();
        pathTreeNodeCollection.add(new PathTreeNode(rootPath));

        PathTreeNodeObservableFactory pathTreeNodeObservableFactory = new PathTreeNodeObservableFactory(new PathTreeNodeFactory(), new PathTreeNodeCollectionFactory());

        pathTreeNodeObservableFactory.createObservableForPath(rootPath).subscribe(pathTreeNode -> {
            assertThat(pathTreeNode, is(new PathTreeNode(rootPath)));
        });
    }

    @Test(expected = CannotCreateObservable.class)
    public void wrapsExceptionFromPathThreeNodeFactory() throws Exception {
        PathTreeNodeFactory pathTreeNodeFactory = mock(PathTreeNodeFactory.class);
        PathTreeNodeObservableFactory pathTreeNodeObservableFactory = new PathTreeNodeObservableFactory(
            pathTreeNodeFactory,
            new PathTreeNodeCollectionFactory()
        );

        Path path = mock(Path.class);
        when(pathTreeNodeFactory.createFromPath(path)).thenThrow(CouldNotCreatePathTreeNode.class);
        pathTreeNodeObservableFactory.createObservableForPath(path);
    }
}
