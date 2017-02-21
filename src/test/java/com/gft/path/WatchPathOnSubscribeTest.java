package com.gft.path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import rx.Subscriber;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public final class WatchPathOnSubscribeTest {

    @Test(timeout = 10000)
    public void watchesPathForChangesAndEmitsThemToTheSubscriber() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        WatchService watchService = fileSystem.newWatchService();

        Path rootDir = fileSystem.getPath("/root");
        Files.createDirectory(rootDir);

        ConcurrentLinkedQueue<Path> emittedPaths = new ConcurrentLinkedQueue<>();

        Executors.newSingleThreadExecutor().submit(() -> {
            WatchPathOnSubscribe watchPathOnSubscribe = new WatchPathOnSubscribe(rootDir, watchService);
            watchPathOnSubscribe.call(new Subscriber<Path>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(Path path) {
                    emittedPaths.add(path);
                }
            });
        });

        Thread.sleep(500);

        Path rootSubDir = rootDir.resolve("rootSubDir");
        Files.createDirectory(rootSubDir);

        Path level2SubDir = rootSubDir.resolve("level2SubDir/somethingelse");
        Files.createDirectories(level2SubDir);

        while (emittedPaths.size() < 3) {
        }

        fileSystem.close();

        Assertions
            .assertThat(emittedPaths)
            .containsOnly(
                rootSubDir,
                level2SubDir,
                rootSubDir.resolve("level2SubDir")
            );
    }
}
