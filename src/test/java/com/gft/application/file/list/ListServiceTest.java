package com.gft.application.file.list;

import com.gft.path.collection.PathTreeNodeCollection;
import com.gft.path.collection.PathTreeNodeCollectionFactory;
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

public class ListServiceTest {

    @Test
    public void createsObservableFromPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fileSystem.getPath("C:\\root");
        Files.createDirectory(rootPath);

        PathTreeNodeCollection pathTreeNodeCollection = new PathTreeNodeCollection();
        pathTreeNodeCollection.add(new PathTreeNode(rootPath));

        ListService listService = new ListService(new PathTreeNodeFactory(), new PathTreeNodeCollectionFactory());

        listService.createObservableForPath(rootPath).subscribe(pathTreeNode -> {
            assertThat(pathTreeNode, is(new PathTreeNode(rootPath)));
        });
    }
}
