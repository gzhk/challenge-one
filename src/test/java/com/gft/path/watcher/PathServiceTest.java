package com.gft.path.watcher;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathServiceTest {
    @Test
    public void name() throws Exception {

//
//        HashMap<WatchKey, Path> watchKeyPath = new HashMap<>();
//        WatchService watchService = mock(WatchService.class);
//        RecursiveWatchServiceFileVisitor recursiveWatchServiceFileVisitor = new RecursiveWatchServiceFileVisitor(watchService, watchKeyPath);
//
//        Path dir = mock(Path.class);
//        WatchKey watchKey = mock(WatchKey.class);
//        when(dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenReturn(watchKey);
//
//        recursiveWatchServiceFileVisitor.preVisitDirectory(dir, mock(BasicFileAttributes.class));
//
//        assertThat(watchKeyPath.get(watchKey), is(dir));

    }
}
