package ru.tattoo.maxsim.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.controller.CRUDController;;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ReviewService;

@Controller
@Slf4j
@RequestMapping("/admin/reviews")
public class ReviewsAdminController extends CRUDController<ReviewsUser, Long> {

    @Autowired
    private ReviewService reviewService;

    @Override
    protected String getEntityName() {
        return "fragment-admin";
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
    private String getGalleryFragment(Model model, HttpServletRequest request) {
        log.info("Получено page {}",
                request.getRequestURL());
        model.addAttribute("reviewsEntity", new ReviewsUser());
        model.addAttribute("reviews", reviewService.findAll());

        return getEntityName()+"::reviews";
    }


}
