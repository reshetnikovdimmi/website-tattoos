package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.service.interf.UserService;

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

    @GetMapping("/admin")
    public String admin(Model model) {

        model.addAttribute("images", imagesService.findAll());
        model.addAttribute("sketches", sketchesService.findAll());
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("user", userService.findAll());

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






}

