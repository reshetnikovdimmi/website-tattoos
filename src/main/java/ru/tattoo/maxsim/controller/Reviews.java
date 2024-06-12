package ru.tattoo.maxsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tattoo.maxsim.model.User;

@Controller
public class Reviews {
    @GetMapping("/reviews")
    public String reviews(Model model) {

        return "reviews";
    }
}
