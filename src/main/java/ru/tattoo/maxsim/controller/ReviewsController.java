package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.service.interf.ReviewService;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;

@Controller
public class ReviewsController {

    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/reviews")
    public String reviews(Model model) {

        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());

        return "reviews";
    }

    @PostMapping("/reviews-import")
    public String reviewsImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("Comment") String Comment, @RequestParam("name") String name, Model model, HttpServletRequest request) throws IOException, ParseException {

        reviewService.saveImd(fileImport,Comment,name);
        model.addAttribute("reviews", reviewService.findAll());

        return "reviews::fragment-reviews";
    }
}
