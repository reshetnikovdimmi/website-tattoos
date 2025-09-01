package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDateTime;

@Controller
@RequestMapping(ReviewsController.URL)
public class ReviewsController extends CRUDController<ReviewsUser, Long> {

    public static final String URL = "/reviews";
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";
    private static final int PAGE_NUMBER = 0;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SketchesService sketchesService;

    @Autowired
    private ImagesService imagesService;

    @GetMapping()
    public String reviews(Model model) {

        model.addAttribute("reviewsEntity", new ReviewsUser());
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));

        return "reviews";
    }


    @PostMapping("/reviews-import")
    public String upload(@ModelAttribute("reviewsEntity") ReviewsUser object, Principal principal,
                         Model model) throws IOException, ParseException {
        object.setUserName(principal.getName());
        getService().create(prepareObject(object));
        updateSection(model);
        return "reviews::fragment-reviews";
    }


    @Override
    String getEntityName() {
        return "admin::reviews";
    }

    @Override
    CRUDService<ReviewsUser, Long> getService() {
        return reviewService;
    }

    @Override
    void updateSection(Model model) {
        model.addAttribute("reviewsEntity", new ReviewsUser());
        model.addAttribute("reviews", reviewService.findAll());
    }
}
