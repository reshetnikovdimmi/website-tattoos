package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;

import java.util.List;

@Controller
public class AboutMeController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ImagesService imagesService;

    @GetMapping("/about-me")
    public String about(Model model) {
        model.addAttribute("reviewsLimit", reviewService.findLimit());
        model.addAttribute("bestTattoos", imagesService.findByFlagTrue());
        return "about-me";
    }

}
