package ru.tattoo.maxsim.controller;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tattoo.maxsim.model.User;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Model model) {
        LoggerFactory.getLogger(LoginController.class).info("/login");
        model.addAttribute("user", new User());
        return "redirect:login";
    }
}
