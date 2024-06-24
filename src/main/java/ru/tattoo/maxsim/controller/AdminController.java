package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.repository.ReviewsUserRepository;
import ru.tattoo.maxsim.repository.SketchesRepository;
import ru.tattoo.maxsim.repository.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Controller

public class AdminController {

    private static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SketchesRepository sketchesRepository;

    @GetMapping("/admin")
    public String admin(Model model) {

        List<Images> images = imagesRepository.findAll();
        Collections.reverse(images);
        List<Sketches> listSketches = sketchesRepository.findAll();
        Collections.reverse(listSketches);
        List<ReviewsUser> reviewsUsers = reviewsUserRepository.findAll();
        Collections.reverse(reviewsUsers);

        model.addAttribute("images", images);
        model.addAttribute("sketches", listSketches);
        model.addAttribute("reviews", reviewsUsers);
        model.addAttribute("user", userRepository.findAll());

        return "admin";
    }

    @PostMapping("/img-import")

    public String imgImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description,@RequestParam("category") String category, Model model, HttpServletRequest request) throws IOException, ParseException {

        Images img = new Images();
        img.setImageName(fileImport.getOriginalFilename());
        img.setDescription(description);
        img.setCategory(category);

        Images uploadImg = imagesRepository.save(img);

        if(uploadImg!=null)saveImg(fileImport);

        List<Images> images = imagesRepository.findAll();
        Collections.reverse(images);
        model.addAttribute("images", images);
        return "admin::img-import";
    }

    @PostMapping("/sketches-import")
    public String sketchesImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description, Model model, HttpServletRequest request) throws IOException, ParseException {

        Sketches sketches = new Sketches();
        sketches.setImageName(fileImport.getOriginalFilename());
        sketches.setDescription(description);

        Sketches uploadImg = sketchesRepository.save(sketches);

        if(uploadImg!=null)saveImg(fileImport);

        List<Sketches> listSketches = sketchesRepository.findAll();
        Collections.reverse(listSketches);
        model.addAttribute("sketches", listSketches);
        return "admin::sketches-import";
    }

    @GetMapping("/sketches-delete/{id}")
    public String deleteSketches(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        deleteImg(sketchesRepository.getName(id));

        sketchesRepository.deleteById(id);

        List<Sketches> listSketches = sketchesRepository.findAll();
        Collections.reverse(listSketches);
        model.addAttribute("sketches", listSketches);
        return "admin::sketches-import";
    }

    @GetMapping("/img-delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        deleteImg(imagesRepository.getName(id));

        imagesRepository.deleteById(id);

        List<Images> images = imagesRepository.findAll();
        Collections.reverse(images);
        model.addAttribute("images", images);

        return "admin::img-import";
    }

    @GetMapping("/reviews-delete/{id}")
    public String deleteReviews(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        deleteImg(reviewsUserRepository.getName(id));

        reviewsUserRepository.deleteById(id);

        List<ReviewsUser> reviewsUsers = reviewsUserRepository.findAll();
        Collections.reverse(reviewsUsers);
        model.addAttribute("reviews", reviewsUsers);

        return "admin::reviews";
    }

    private void deleteImg(String name) throws IOException {
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , name);
        Files.delete(fileNameAndPath);
    }
    private void saveImg(MultipartFile fileImport) throws IOException {
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , fileImport.getOriginalFilename());
        Files.write(fileNameAndPath, fileImport.getBytes());
    }

}

