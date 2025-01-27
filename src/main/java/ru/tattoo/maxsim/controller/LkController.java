package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.service.interf.ImagesService;

@Controller
public class LkController {

    @Autowired
    private ImagesService imagesService;

    @GetMapping("/lk")
    public String login(Model model) {
        model.addAttribute("images", imagesService.findAll());
        return "lk";
    }
    @GetMapping("/user-info")
    public String userInfo(Model model) {
        return "fragments :: second-fragment";
    }
    @GetMapping("/profile-editing")
    public String profileEditing(Model model) {
        return "fragments :: profile-editing";
    }

}
