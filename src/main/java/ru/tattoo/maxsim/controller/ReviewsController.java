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
import ru.tattoo.maxsim.service.Reverse;
import ru.tattoo.maxsim.service.SaveImg;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class ReviewsController implements SaveImg, Reverse {

    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";

    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @GetMapping("/reviews")
    public String reviews(Model model) {

        model.addAttribute("reviews", reverse(reviewsUserRepository.findAll()));
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

        reviewsUserRepository.save(reviewsUser);

        saveImg(fileImport,UPLOAD_DIRECTORY);

        model.addAttribute("reviews", reverse(reviewsUserRepository.findAll()));
        return "reviews::fragment-reviews";
    }
}
