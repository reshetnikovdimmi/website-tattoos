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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Controller

public class AdminController {

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
        model.addAttribute("images", images);

        model.addAttribute("reviews", reviewsUserRepository.findAll());
        model.addAttribute("user", userRepository.findAll());
        List<Sketches> listSketches = sketchesRepository.findAll();
        Collections.reverse(listSketches);
        model.addAttribute("sketches", listSketches);
        return "admin";
    }

    @PostMapping("/img-import")

    public String imgImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description,@RequestParam("category") String category, Model model, HttpServletRequest request) throws IOException, ParseException {

        Images img = new Images();

        img.setImageName(fileImport.getOriginalFilename());
        img.setDescription(description);
        img.setCategory(category);
        Images uploadImg = imagesRepository.save(img);
        if(uploadImg!=null){
            try {
                File file = new File(request.getServletContext().getRealPath("WEB-INF/views/img/gallery"));
                Path path = Paths.get(file.getAbsolutePath()+File.separator+fileImport.getOriginalFilename());
                Files.copy(fileImport.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

            }catch (Exception e){

                e.printStackTrace();
            }
        }
        List<Images> images = imagesRepository.findAll();
        Collections.reverse(images);
        model.addAttribute("images", images);

        model.addAttribute("reviews", reviewsUserRepository.findAll());
        return "admin::img-import";
    }

    @PostMapping("/sketches-import")
    public String sketchesImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description, Model model, HttpServletRequest request) throws IOException, ParseException {
        Sketches sketches = new Sketches();
        sketches.setImageName(fileImport.getOriginalFilename());
        sketches.setDescription(description);
        Sketches uploadImg = sketchesRepository.save(sketches);
        if(uploadImg!=null){
            try {
                File file = new File(request.getServletContext().getRealPath("WEB-INF/views/img/sketches"));
                Path path = Paths.get(file.getAbsolutePath()+File.separator+fileImport.getOriginalFilename());
                Files.copy(fileImport.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

            }catch (Exception e){

                e.printStackTrace();
            }
        }
        List<Images> images = imagesRepository.findAll();
        Collections.reverse(images);
        model.addAttribute("images", images);
        model.addAttribute("reviews", reviewsUserRepository.findAll());
        model.addAttribute("user", userRepository.findAll());
        List<Sketches> listSketches = sketchesRepository.findAll();
        Collections.reverse(listSketches);
        model.addAttribute("sketches", listSketches);
        return "admin::sketches-import";
    }

    @GetMapping("/sketches-delete")
    public String deleteSketches(@RequestParam("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        try {
            File file = new File(request.getServletContext().getRealPath("WEB-INF/views/img/sketches"));
            Path path = Paths.get(file.getAbsolutePath()+File.separator+imagesRepository.getName(id));
            Files.delete(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        sketchesRepository.deleteById(id);
        model.addAttribute("reviews", reviewsUserRepository.findAll());
        model.addAttribute("images", imagesRepository.findAll());
        model.addAttribute("user", userRepository.findAll());
        model.addAttribute("sketches", sketchesRepository.findAll());
        return "admin";
    }

    @GetMapping("/img-delete")
    public String delete(@RequestParam("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        try {
            File file = new File(request.getServletContext().getRealPath("WEB-INF/views/img/gallery"));
            Path path = Paths.get(file.getAbsolutePath()+File.separator+imagesRepository.getName(id));
            Files.delete(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        imagesRepository.deleteById(id);
        List<Images> images = imagesRepository.findAll();
        Collections.reverse(images);

        model.addAttribute("reviews", reviewsUserRepository.findAll());
        model.addAttribute("images", images);
        return "admin";
    }

    @GetMapping("/reviews-delete")
    public String deleteReviews(@RequestParam("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        try {
            File file = new File(request.getServletContext().getRealPath("WEB-INF/views/img/user-comment"));
            Path path = Paths.get(file.getAbsolutePath()+File.separator+reviewsUserRepository.getName(id));
            Files.delete(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        reviewsUserRepository.deleteById(id);
        model.addAttribute("reviews", reviewsUserRepository.findAll());
        model.addAttribute("images", imagesRepository.findAll());
        return "admin";
    }
}

