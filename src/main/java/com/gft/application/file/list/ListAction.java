package com.gft.application.file.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public final class ListAction {

    private final String dir;

    @Autowired
    public ListAction(@Value("${dir}") String dir) {
        this.dir = dir;
    }

    @GetMapping("/list")
    public String invoke(Model model) {
        model.addAttribute("dir", dir);
        model.addAttribute("token", UUID.randomUUID().toString());

        return "file/list";
    }
}
