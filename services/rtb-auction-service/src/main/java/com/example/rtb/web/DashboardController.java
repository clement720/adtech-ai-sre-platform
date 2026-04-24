package com.example.rtb.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String home() {
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
