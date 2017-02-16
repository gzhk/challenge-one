package com.gft.application.file.add;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Component
public class PathUtils {

    public boolean exists(@NotNull final Path path) {
        return Files.exists(path);
    }

    public void createFile(@NotNull final Path path) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, Collections.singletonList(""), Charset.forName("UTF-8"));
    }
}
