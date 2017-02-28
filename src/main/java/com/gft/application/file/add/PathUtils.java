package com.gft.application.file.add;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Collections;

public class PathUtils {

    public boolean exists(@NotNull final Path path) {
        return Files.exists(path);
    }

    public void createEmptyFile(@NotNull final Path path) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, Collections.singletonList(""));
    }

    public void registerDirectoriesRecursively(@NotNull final Path rootPath, @NotNull final WatchService watchService) {
        try {
            Files.walk(rootPath).forEach(path -> {
                try {
                    if (Files.isDirectory(path)) {
                        path.register(
                            watchService,
                            new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE},
                            SensitivityWatchEventModifier.HIGH
                        );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
