package com.gft.application.file.add;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public final class AddFileActionTest {

    @Test
    public void doesNothingIfFileAlreadyExists() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path directory = fileSystem.getPath("/tmp");
        Files.createDirectory(directory);

        Path file = directory.resolve("file.txt");
        Files.write(file, Collections.singletonList(""), Charset.forName("UTF-8"));

        AddFileAction addFileAction = new AddFileAction(directory, new PathUtils());

        ResponseEntity<String> responseEntity = addFileAction.invoke(file);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is("File already exists."));
    }

    @Test
    public void createsFile() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path directory = fileSystem.getPath("/tmp");
        Files.createDirectory(directory);

        AddFileAction addFileAction = new AddFileAction(directory, new PathUtils());
        ResponseEntity<String> responseEntity = addFileAction.invoke(directory.resolve("file.txt"));

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is("File created."));
        assertTrue(Files.exists(directory.resolve("file.txt")));
    }

    @Test
    public void returnsErrorMessageWithStackTrace() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path directory = fileSystem.getPath("/tmp");
        Files.createDirectory(directory);

        PathUtils pathUtils = mock(PathUtils.class);
        AddFileAction addFileAction = new AddFileAction(directory, pathUtils);
        Path path = directory.resolve("file.txt");
        IOException ioException = new IOException("Exception message");
        doThrow(ioException).when(pathUtils).createEmptyFile(any());

        ResponseEntity<String> responseEntity = addFileAction.invoke(path);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), containsString("Exception message"));
    }
}
