package ru.tattoo.maxsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.model.User;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
}
