package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tattoo.maxsim.service.interf.ReviewService;

import java.time.LocalDateTime;

@Controller
public class BlogController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping ("/blog")
    public  String blog (Model model){
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        return "blog";
    }
}
