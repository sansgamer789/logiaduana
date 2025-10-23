package com.logiaduana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/public")
    public String publicPage() {
        return "public";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}