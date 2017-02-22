package com.gft.path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathUtilsTest {

    @Test
    public void returnsTrueIfPathExists() throws Exception {
        PathUtils pathUtils = new PathUtils();

        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path path = fileSystem.getPath("/tmp");
        Files.createDirectories(path);

        assertTrue(pathUtils.exists(path));
    }

    @Test
    public void returnsFalseIfPathDoesNotExists() throws Exception {
        PathUtils pathUtils = new PathUtils();
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        assertFalse(pathUtils.exists(fileSystem.getPath("/tmp")));
    }

    @Test
    public void createsPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path path = fileSystem.getPath("/tmp/file.txt");

        PathUtils pathUtils = new PathUtils();

        assertFalse(pathUtils.exists(path));
        pathUtils.createEmptyFile(path);
        assertTrue(pathUtils.exists(path));
    }
}
