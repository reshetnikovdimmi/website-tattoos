package ru.tattoo.maxsim.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.ReviewSection;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ReviewSectionService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin/review-section")
public class ReviewSectionController extends CRUDController <ReviewSection, Long>{

    @Autowired
    private ReviewSectionService reviewSectionService;


    @Override
    protected String getFragmentName() {
        return "fragment-admin";
    }

    @Override
    protected CRUDService<ReviewSection, Long> getService() {
        return reviewSectionService;
    }

    @Override
    protected void updateSection(Model model) {
        List<ReviewSection> sections = reviewSectionService.findAll();
        log.info("Получено {} секций: {}", sections.size(), sections);
        model.addAttribute("reviewsEntity", sections.get(0));

    }
}
