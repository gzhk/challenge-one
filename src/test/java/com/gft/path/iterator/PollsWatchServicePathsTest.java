package com.gft.path.iterator;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.sun.nio.file.SensitivityWatchEventModifier;
import edu.emory.mathcs.backport.java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public final class PollsWatchServicePathsTest {

    @Test(timeout = 10000)
    public void containsPolledPaths() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootPath = fileSystem.getPath("/root");
        Files.createDirectory(rootPath);
        rootPath.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);

        PollsWatchServicePaths pollsWatchServicePaths = new PollsWatchServicePaths(watchService);

        Path multiLevelPath = rootPath.resolve("level1/level2/level3");
        Files.createDirectories(multiLevelPath);

        Path file = rootPath.resolve("file.txt");
        Files.write(file, Collections.singleton("line"), Charset.forName("UTF-8"));

        List<Path> returned = new ArrayList<>();

        while (returned.size() < 4 && pollsWatchServicePaths.hasNext()) {
            returned.add(pollsWatchServicePaths.next());
        }

        Assertions
            .assertThat(returned)
            .containsOnly(
                rootPath.resolve("level1"),
                rootPath.resolve("level1/level2"),
                rootPath.resolve("level1/level2/level3"),
                file
            );
    }

    @Test
    public void returnsFalseIfWatchKeyIsNoLongerValid() throws Exception {
        WatchService watchService = Mockito.mock(WatchService.class);

        WatchKey watchKey = Mockito.mock(WatchKey.class);
        Mockito.when(watchService.take()).thenReturn(watchKey);
        Mockito.when(watchKey.pollEvents()).thenReturn(new ArrayList<>());
        Mockito.when(watchKey.reset()).thenReturn(false);

        PollsWatchServicePaths pollsWatchServicePaths = new PollsWatchServicePaths(watchService);

        Assert.assertFalse(pollsWatchServicePaths.hasNext());
    }

    @Test
    public void closesWatchServiceAndDoesNotHaveNextPaths() throws Exception {
        WatchService watchService = Mockito.mock(WatchService.class);

        PollsWatchServicePaths pollsWatchServicePaths = new PollsWatchServicePaths(watchService);
        pollsWatchServicePaths.close();

        verify(watchService, times(1)).close();

        Assert.assertFalse(pollsWatchServicePaths.hasNext());
    }
}
