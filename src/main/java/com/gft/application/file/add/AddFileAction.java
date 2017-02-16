package com.gft.application.file.add;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

@RestController
public final class AddFileAction {

    private final Path directory;
    private final PathUtils pathUtils;

    @Autowired
    public AddFileAction(@Value("${dir}") final Path directory, @NotNull final PathUtils pathUtils) {
        this.directory = directory;
        this.pathUtils = pathUtils;
    }

    @GetMapping("/addFile")
    public ResponseEntity<String> invoke(@RequestParam(name = "name") Path path) {
        path = directory.resolve(path);

        if (pathUtils.exists(path)) {
            return ResponseEntity.ok("File already exists.");
        }

        try {
            pathUtils.createFile(path);
        } catch (IOException e) {
            return ResponseEntity.ok(e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
        }

        return ResponseEntity.ok("File created.");
    }
}
