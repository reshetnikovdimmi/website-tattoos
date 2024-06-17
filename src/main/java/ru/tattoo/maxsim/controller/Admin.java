package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.repository.ReviewsUserRepository;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Optional;

@Controller
public class Admin  {

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("images", imagesRepository.findAll());
        model.addAttribute("reviews", reviewsUserRepository.findAll());
        return "admin";
    }

    @PostMapping("/img-import")
    public String imgImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description,@RequestParam("category") String category, Model model, HttpServletRequest request) throws IOException, ParseException {
        Images images = new Images();
        images.setImageName(fileImport.getOriginalFilename());
        images.setDescription(description);
        images.setCategory(category);
        Images uploadImg = imagesRepository.save(images);
        if(uploadImg!=null){
            try {
                File file = new File(request.getServletContext().getRealPath("WEB-INF/views/img/gallery"));
                Path path = Paths.get(file.getAbsolutePath()+File.separator+fileImport.getOriginalFilename());
                Files.copy(fileImport.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        model.addAttribute("images", imagesRepository.findAll());
        model.addAttribute("reviews", reviewsUserRepository.findAll());
        return "/admin";
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
        model.addAttribute("reviews", reviewsUserRepository.findAll());
        model.addAttribute("images", imagesRepository.findAll());
        return "/admin";
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
        return "/admin";
    }
}

