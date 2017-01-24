package com.gft.application.file.list;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public final class ListAction {

    @GetMapping("/list")
    public String invoke() {
        return "file/list";
    }
}
