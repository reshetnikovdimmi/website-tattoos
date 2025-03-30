package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDateTime;

@Controller
public class ReviewsController {

    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";
    private static final int PAGE_NUMBER = 0;
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SketchesService sketchesService;

    @Autowired
    private ImagesService imagesService;

    @GetMapping("/reviews")
    public String reviews(Model model) {

        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        model.addAttribute("gallery", imagesService.pageList(null,null,PageSize.IMG_9.getPageSize(),PAGE_NUMBER));


        return "reviews";
    }

    @PostMapping("/reviews-import")
    public String reviewsImport(@RequestParam("imageName") String imageName, @RequestParam("Comment") String Comment, Principal principal, Model model, HttpServletRequest request) throws IOException, ParseException {

        reviewService.saveImd(imageName,Comment,principal.getName());
        model.addAttribute("reviews", reviewService.findAll());

        return "reviews::fragment-reviews";
    }


}
