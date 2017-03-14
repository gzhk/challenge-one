package com.gft.application.file.add;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddFileServiceTest {

    @Test
    public void returnsTrueIfPathExists() throws Exception {
        AddFileService addFileService = new AddFileService();

        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path path = fileSystem.getPath("/tmp");
        Files.createDirectories(path);

        assertTrue(addFileService.exists(path));
    }

    @Test
    public void returnsFalseIfPathDoesNotExists() throws Exception {
        AddFileService addFileService = new AddFileService();
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        assertFalse(addFileService.exists(fileSystem.getPath("/tmp")));
    }

    @Test
    public void createsPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path path = fileSystem.getPath("/tmp/file.txt");

        AddFileService addFileService = new AddFileService();

        assertFalse(addFileService.exists(path));
        addFileService.createEmptyFile(path);
        assertTrue(addFileService.exists(path));
    }
}
