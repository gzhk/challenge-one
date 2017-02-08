package com.gft.path;

import com.gft.node.CannotRetrieveChildren;
import com.gft.path.PathNode;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class PathNodeTest {

    @Test
    public void returnsPathPassedInConstructor() throws Exception {
        Path path = mock(Path.class);
        PathNode pathNode = new PathNode(path);

        assertThat(pathNode.getPayload(), is(path));
    }

    @Test
    public void returnsTrueIfPathsAreEqual() throws Exception {
        Path path = mock(Path.class);

        assertThat(new PathNode(path), is(new PathNode(path)));
    }

    @Test
    public void returnsFalseIfPathsAreNotEqual() throws Exception {
        assertThat(new PathNode(mock(Path.class)), is(not(new PathNode(mock(Path.class)))));
    }

    @Test
    public void returnsHashCodeComputedBasedOnPath() throws Exception {
        Path path = mock(Path.class);

        assertThat(new PathNode(path).hashCode(), is(Objects.hash(path)));
    }

    @Test
    public void returnsEmptyCollectionWhenNodeDoesNotHaveAnyChildren() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path path = fileSystem.getPath("/root");
        Files.createDirectory(path);

        PathNode pathNode = new PathNode(path);

        assertThat(pathNode.children().size(), is(0));
    }

    @Test
    public void returnsCollectionWithChildren() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        Path directory = rootPath.resolve("directory");
        Files.createDirectory(directory);

        Path file = rootPath.resolve("file.txt");
        Files.write(file, ImmutableList.of("content"), StandardCharsets.UTF_8);

        assertThat(new PathNode(rootPath).children(), hasItems(new PathNode(directory), new PathNode(file)));
    }

    @Test
    public void returnsEmptyListIfPathIsNotADirectory() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path file = fileSystem.getPath("file.txt");
        Files.write(file, ImmutableList.of("content"), StandardCharsets.UTF_8);

        PathNode pathNode = new PathNode(file);

        assertThat(pathNode.children(), is(new ArrayList<>()));
    }
}
