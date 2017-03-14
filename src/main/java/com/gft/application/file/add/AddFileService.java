package com.gft.application.file.add;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Service
public class AddFileService {

    public boolean exists(@NotNull final Path path) {
        return Files.exists(path);
    }

    public void createEmptyFile(@NotNull final Path path) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, Collections.singletonList(""));
    }
}
