package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
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

    @GetMapping("/reviews")
    public String reviews(Model model) {

        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        Page<Sketches> images = sketchesService.partition(PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize()));
        model.addAttribute("number", PageSize.IMG_9.getPageSize());
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("images1", sketchesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());

        return "reviews";
    }

    @PostMapping("/reviews-import")
    public String reviewsImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("Comment") String Comment, @RequestParam("name") String name, Model model, HttpServletRequest request) throws IOException, ParseException {

        reviewService.saveImd(fileImport,Comment,name);
        model.addAttribute("reviews", reviewService.findAll());

        return "reviews::fragment-reviews";
    }
}
