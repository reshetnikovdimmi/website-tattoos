package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.InterestingWorks;
import ru.tattoo.maxsim.repository.CommitsRepository;
import ru.tattoo.maxsim.service.interf.*;

import java.io.IOException;
import java.text.ParseException;


@Controller

public class AdminController {

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private SketchesService sketchesService;

    @Autowired
    private InterestingWorksService interestingWorksService;

    @Autowired
    private CommitsService commitsService;

    @Autowired
    private HomeService homeService;


    @GetMapping("/admin")
    public String admin(Model model) {

        model.addAttribute("images", imagesService.findAll());
        model.addAttribute("sketches", sketchesService.findAll());
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("user", userService.findAll());
        model.addAttribute("flag", true);
        model.addAttribute("interestingWorks", interestingWorksService.findAll());
        model.addAttribute("commits", commitsService.findAll());
        model.addAttribute("carousel", homeService.findAll());

        return "admin";
    }

    @PostMapping("/img-import")

    public String imgImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description,@RequestParam("category") String category, Model model, HttpServletRequest request) throws IOException, ParseException {

        imagesService.saveImg(fileImport,description,category);
        model.addAttribute("images", imagesService.findAll());

        return "admin::img-import";
    }

    @PostMapping("/sketches-import")
    public String sketchesImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description, Model model, HttpServletRequest request) throws IOException, ParseException {

        sketchesService.saveImg(fileImport,description);
        model.addAttribute("sketches", sketchesService.findAll());

        return "admin::sketches-import";
    }

    @PostMapping("/carousel-import")
    public String carouselImport(@RequestParam("file") MultipartFile fileImport, Model model, HttpServletRequest request) throws IOException, ParseException {

        homeService.saveImg(fileImport, "carousel");
        model.addAttribute("carousel", homeService.findAll());

        return "admin::carousel-import";
    }

    @GetMapping("/carousel-delete/{id}")
    public String deleteCarousel(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        homeService.deleteImg(id);
        model.addAttribute("carousel", homeService.findAll());

        return "admin::carousel-import";
    }

    @PostMapping("/interesting-works-import")
    public String interestingWorksImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description, Model model, HttpServletRequest request) throws IOException, ParseException {

        interestingWorksService.saveImg(fileImport,description);
        model.addAttribute("interestingWorks", interestingWorksService.findAll());

        return "admin::interesting-works-import";
    }

    @GetMapping("/interesting-works-delete/{id}")
    public String deleteInterestingWorks(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        interestingWorksService.deleteImg(id);
        model.addAttribute("interestingWorks", interestingWorksService.findAll());

        return "admin::interesting-works-import";
    }

    @GetMapping("/sketches-delete/{id}")
    public String deleteSketches(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        sketchesService.deleteImg(id);
        model.addAttribute("sketches", sketchesService.findAll());

        return "admin::sketches-import";
    }

    @GetMapping("/img-delete/{id}")
    public String deleteImg(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        imagesService.deleteImg(id);
        model.addAttribute("images", imagesService.findAll());

        return "admin::img-import";
    }

    @GetMapping("/reviews-delete/{id}")
    public String deleteReviews(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        reviewService.deleteImg(id);
        model.addAttribute("reviews", reviewService.findAll());

        return "admin::reviews";
    }


    //Response controller

    @PostMapping(path = "/best-tattoos")
    private ResponseEntity saveSparkSale(@RequestBody Images images) {
        return ResponseEntity.ok(imagesService.bestImage(images));
    }




}

