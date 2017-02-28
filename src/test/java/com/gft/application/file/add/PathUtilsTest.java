package com.gft.application.file.add;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
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

    @Test(timeout = 10000)
    public void registersPathRecursivelyInWatchService() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);

        Path subPath = rootPath.resolve("subPath");
        Files.createDirectory(subPath);

        WatchService watchService = fileSystem.newWatchService();

        PathUtils pathUtils = new PathUtils();
        pathUtils.registerDirectoriesRecursively(rootPath, watchService);

        Path newRootPath = rootPath.resolve("newRootPath");
        Files.createDirectory(newRootPath);

        Path newSubPath = rootPath.resolve("newSubPath");
        Files.createDirectory(newSubPath);

        List<Path> emittedPaths = new ArrayList<>();

        while (emittedPaths.size() < 2) {
            WatchKey watchKey = watchService.poll();

            if (watchKey == null) {
                continue;
            }

            watchKey.pollEvents()
                .stream()
                .filter(watchEvent -> watchEvent.kind() != OVERFLOW)
                .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
                .map(path -> ((Path) watchKey.watchable()).resolve(path))
                .forEach(emittedPaths::add);
        }

        Assertions.assertThat(emittedPaths).containsOnly(newRootPath, newSubPath);
    }
}
