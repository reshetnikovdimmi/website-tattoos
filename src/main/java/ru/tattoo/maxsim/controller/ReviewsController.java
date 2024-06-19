package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.repository.ReviewsUserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Controller
public class ReviewsController {

    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewsUserRepository.findAll());
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewsUserRepository.getCount());
        return "reviews";
    }

    @PostMapping("/reviews-import")
    public String reviewsImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("Comment") String Comment, @RequestParam("name") String name, Model model, HttpServletRequest request) throws IOException, ParseException {

       ReviewsUser reviewsUser = new ReviewsUser();
        reviewsUser.setImageName(fileImport.getOriginalFilename());
        reviewsUser.setComment(Comment);
        reviewsUser.setUserName(name);
        reviewsUser.setDate(new Date());
        ReviewsUser uploadImg = reviewsUserRepository.save(reviewsUser);
        if(uploadImg!=null){
            try {
                File file = new File(request.getServletContext().getRealPath("WEB-INF/views/img/user-comment"));
                Path path = Paths.get(file.getAbsolutePath()+File.separator+fileImport.getOriginalFilename());
                Files.copy(fileImport.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        model.addAttribute("reviews", reviewsUserRepository.findAll());
        return "/reviews";
    }
}
