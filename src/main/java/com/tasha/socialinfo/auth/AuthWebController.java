package com.tasha.socialinfo.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthWebController {
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/")
    public String signIn() {
        return "redirect:/students";
    }
}
