package ru.tattoo.maxsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ContactController {
    @GetMapping("/contact")
    public String gallery(Model model) {
        return "contact";
    }
}
