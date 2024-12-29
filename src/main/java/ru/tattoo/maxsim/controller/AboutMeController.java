package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tattoo.maxsim.service.interf.ReviewService;

@Controller
public class AboutMeController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/about-me")
    public String about(Model model) {
        model.addAttribute("reviewsLimit", reviewService.findLimit());
        return "about-me";
    }
}
