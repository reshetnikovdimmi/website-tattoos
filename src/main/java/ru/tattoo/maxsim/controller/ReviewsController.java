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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class ReviewsController {

    private static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";

    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @GetMapping("/reviews")
    public String reviews(Model model) {
        List<ReviewsUser> reviews = reviewsUserRepository.findAll();
        Collections.reverse(reviews);
        model.addAttribute("reviews", reviews);
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
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , fileImport.getOriginalFilename());
            Files.write(fileNameAndPath, fileImport.getBytes());
        }

        List<ReviewsUser> reviews = reviewsUserRepository.findAll();
        Collections.reverse(reviews);
        model.addAttribute("reviews", reviews);
        return "reviews::fragment-reviews";
    }
}
