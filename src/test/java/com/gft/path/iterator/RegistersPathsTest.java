package com.gft.path.iterator;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public final class RegistersPathsTest {

    @Test
    public void callsCloseMethodFromDecoratedObject() throws Exception {
        WatchServiceIterator innerWatchServiceIterator = Mockito.mock(WatchServiceIterator.class);
        WatchService watchService = Mockito.mock(WatchService.class);

        RegistersPaths registersPaths = new RegistersPaths(innerWatchServiceIterator, watchService);
        registersPaths.close();

        Mockito.verify(innerWatchServiceIterator, Mockito.times(1)).close();
        Mockito.verify(watchService, Mockito.times(1)).close();
    }

    @Test
    public void returnsResultFromDecoratedHasNextMethod() throws Exception {
        WatchServiceIterator innerWatchServiceIterator = Mockito.mock(WatchServiceIterator.class);
        WatchService watchService = Mockito.mock(WatchService.class);

        RegistersPaths registersPaths = new RegistersPaths(innerWatchServiceIterator, watchService);
        registersPaths.hasNext();

        Mockito.verify(innerWatchServiceIterator, Mockito.times(1)).hasNext();
    }

    @Test(timeout = 10000)
    public void registersPathsReturnedByDecoratedIteratorInWatchService() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path path = fileSystem.getPath("/root");
        Files.createDirectory(path);
        WatchServiceIterator innerWatchServiceIterator = new WatchServiceIterator() {
            @Override
            public void close() throws Exception {
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Path next() {
                return path;
            }
        };
        WatchService watchService = fileSystem.newWatchService();

        RegistersPaths registersPaths = new RegistersPaths(innerWatchServiceIterator, watchService);
        Path nextPath = registersPaths.next();

        Path createdPath = path.resolve("tmp");
        Files.createDirectory(createdPath);

        WatchKey watchKey = watchService.take();
        List<Path> collect = watchKey
            .pollEvents()
            .stream()
            .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
            .map(p -> ((Path) watchKey.watchable()).resolve(p))
            .collect(Collectors.toList());

        Assertions.assertThat(nextPath).isEqualTo(path);
        Assertions.assertThat(collect).containsOnly(createdPath);
    }
}
