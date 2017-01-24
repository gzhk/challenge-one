package com.gft.application.file.add;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@RestController
public final class AddFileAction {

    private final String directory;

    @Autowired
    public AddFileAction(@Value("${dir}") String directory) {
        this.directory = directory;
    }

    @GetMapping("/addFile")
    public ResponseEntity<String> invoke(@RequestParam String name) {
        Path path = Paths.get(directory + "/" + name);

        if (Files.exists(path)) {
            return ResponseEntity.ok("File already exists.");
        }

        try {
            path.toFile().getParentFile().mkdirs();
            Files.write(path, Arrays.asList(""), Charset.forName("UTF-8"));
        } catch (IOException e) {
            return ResponseEntity.ok(e.toString());
        }

        return ResponseEntity.ok("File created.");
    }
}
