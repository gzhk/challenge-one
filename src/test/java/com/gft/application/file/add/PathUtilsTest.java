package com.gft.application.file.add;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

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
}
