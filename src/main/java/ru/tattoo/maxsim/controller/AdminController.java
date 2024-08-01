package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.repository.ReviewsUserRepository;
import ru.tattoo.maxsim.repository.SketchesRepository;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.DeleteImg;
import ru.tattoo.maxsim.service.Reverse;
import ru.tattoo.maxsim.service.SaveImg;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.UserService;

import java.io.IOException;
import java.text.ParseException;


@Controller

public class AdminController implements DeleteImg, SaveImg, Reverse {

    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SketchesRepository sketchesRepository;

    @GetMapping("/admin")
    public String admin(Model model) {

        model.addAttribute("images", imagesService.findAll());
        model.addAttribute("sketches", reverse(sketchesRepository.findAll()));
        model.addAttribute("reviews", reverse(reviewsUserRepository.findAll()));
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

        Sketches sketches = new Sketches();
        sketches.setImageName(fileImport.getOriginalFilename());
        sketches.setDescription(description);

        sketchesRepository.save(sketches);

        saveImg(fileImport, UPLOAD_DIRECTORY);

        model.addAttribute("sketches", reverse(sketchesRepository.findAll()));
        return "admin::sketches-import";
    }

    @GetMapping("/sketches-delete/{id}")
    public String deleteSketches(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        deleteImg(sketchesRepository.getName(id),UPLOAD_DIRECTORY);
        sketchesRepository.deleteById(id);
        model.addAttribute("sketches", reverse(sketchesRepository.findAll()));

        return "admin::sketches-import";
    }

    @GetMapping("/img-delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        imagesService.deleteImg(id);
        model.addAttribute("images", imagesService.findAll());

        return "admin::img-import";
    }

    @GetMapping("/reviews-delete/{id}")
    public String deleteReviews(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        deleteImg(reviewsUserRepository.getName(id),UPLOAD_DIRECTORY);
        reviewsUserRepository.deleteById(id);
        model.addAttribute("reviews", reverse(reviewsUserRepository.findAll()));

        return "admin::reviews";
    }






}

