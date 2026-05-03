package ru.tattoo.maxsim.controller.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;


@Controller
@Slf4j
@RequestMapping(ReviewsController.URL)
public class ReviewsController extends CRUDController<ReviewsUser, Long> {

    public static final String URL = "/reviews";
    private static final int PAGE_NUMBER = 0;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SketchesService sketchesService;

    @Autowired
    private ImagesService imagesService;

    @Override
    protected String getEntityName() {
          return "reviews";
    }

    @Override
    protected CRUDService<ReviewsUser, Long> getService() {
        return reviewService;
    }

    @Override
    protected void updateSection(Model model) {
        model.addAttribute("reviewsEntity", new ReviewsUser());
        model.addAttribute("reviews", reviewService.findAll());
    }

    @GetMapping()
    public String reviews(Model model) {

        model.addAttribute("reviewsEntity", new ReviewsUser());
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));

        return getEntityName();
    }

    @PostMapping("/import")
    public String createEntity(@ModelAttribute() ReviewsUser object,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model) throws IOException, ParseException {

        log.info("object={}",object);

        getService().create(object);
        updateSection(model);

        return getEntityName() + "::" + fragmentName;
    }


}
