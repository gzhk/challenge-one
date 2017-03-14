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
    private final AddFileService addFileService;

    @Autowired
    public AddFileAction(@Value("${dir}") final Path directory, @NotNull final AddFileService addFileService) {
        this.directory = directory;
        this.addFileService = addFileService;
    }

    @GetMapping("/addFile")
    public ResponseEntity<String> invoke(@RequestParam(name = "name") Path path) {
        final Path absoluteFilePath = directory.resolve(path);

        if (addFileService.exists(absoluteFilePath)) {
            return ResponseEntity.ok("File already exists.");
        }

        try {
            addFileService.createEmptyFile(absoluteFilePath);
        } catch (IOException e) {
            return ResponseEntity.ok(e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
        }

        return ResponseEntity.ok("File created.");
    }
}
