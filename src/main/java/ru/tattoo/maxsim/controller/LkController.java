package ru.tattoo.maxsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tattoo.maxsim.model.User;

@Controller
public class LkController {
    @GetMapping("/lk")
    public String login(Model model) {

        return "lk";
    }
}
