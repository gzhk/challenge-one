package com.gft.path.treenode;

import com.gft.path.treenode.PathTreeNode;
import com.gft.path.treenode.PathTreeNodeFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PathTreeNodeFactoryTest {

    @Test
    public void createsPathTreeNodeFromPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fileSystem.getPath("/tmp");
        Files.createDirectory(rootPath);

        Path directory = rootPath.resolve("directory");
        Files.createDirectory(directory);

        Path subDirectory = directory.resolve("subdirectory");
        Files.createDirectory(subDirectory);

        Path file = rootPath.resolve("file.txt");
        Files.write(file, ImmutableList.of("content"), StandardCharsets.UTF_8);

        ArrayList<PathTreeNode> rootChildren = new ArrayList<>();
        PathTreeNode rootPathTreeNode = new PathTreeNode(rootPath, rootChildren);

        ArrayList<PathTreeNode> directoryChildren = new ArrayList<>();
        PathTreeNode directoryPathTreeNode = new PathTreeNode(directory, rootPathTreeNode, directoryChildren);
        rootChildren.add(directoryPathTreeNode);

        PathTreeNode subDirectoryPathTreeNode = new PathTreeNode(subDirectory, directoryPathTreeNode);
        directoryChildren.add(subDirectoryPathTreeNode);

        PathTreeNode filePathTreeNode = new PathTreeNode(file, rootPathTreeNode);
        rootChildren.add(filePathTreeNode);

        PathTreeNodeFactory pathTreeNodeFactory = new PathTreeNodeFactory();
        assertThat(pathTreeNodeFactory.createFromPath(rootPath), is(rootPathTreeNode));
    }
}
