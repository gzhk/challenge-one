package com.gft.path.collection;

import com.gft.path.PathTreeNode;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PathTreeNodeIteratorTest {

    @Test
    public void itIteratesThroughPathFilesAndDirectories() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fileSystem.getPath("C:\\root");
        Files.createDirectory(rootPath);

        Path directory = rootPath.resolve("directory");
        Files.createDirectory(directory);

        Path file = rootPath.resolve("file.txt");
        Files.write(file, ImmutableList.of("content"), StandardCharsets.UTF_8);

        Path subDirectory = directory.resolve("subdirectory");
        Files.createDirectory(subDirectory);

        PathTreeNode rootPathTreeNode = new PathTreeNode(rootPath);
        PathTreeNode directoryPathTreeNode = new PathTreeNode(directory);
        PathTreeNode subDirectoryPathTreeNode = new PathTreeNode(subDirectory);
        PathTreeNode filePathTreeNode = new PathTreeNode(file);

        PathTreeNodeIterator pathIterator = new PathTreeNodeIterator(rootPathTreeNode, filePathTreeNode);

        assertTrue(pathIterator.hasNext());
        assertThat(pathIterator.next(), is(rootPathTreeNode));

        assertTrue(pathIterator.hasNext());
        assertThat(pathIterator.next(), is(directoryPathTreeNode));

        assertTrue(pathIterator.hasNext());
        assertThat(pathIterator.next(), is(subDirectoryPathTreeNode));

        assertTrue(pathIterator.hasNext());
        assertThat(pathIterator.next(), is(filePathTreeNode));

        assertFalse(pathIterator.hasNext());
    }

    @Test
    public void canCreateEmptyIterator() throws Exception {
        PathTreeNodeIterator iterator = new PathTreeNodeIterator();

        assertFalse(iterator.hasNext());
        assertNull(iterator.next());
    }
}
