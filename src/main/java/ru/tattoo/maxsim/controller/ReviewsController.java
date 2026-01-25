package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Collection;

import static ru.tattoo.maxsim.model.UserRole.ADMIN;

@Controller
@Slf4j
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


    @Override
    String getEntityName() {
        // Минимальное логирование для продакшена
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.trace("Неаутентифицированный доступ к отзывам");
            return "reviews::fragment-reviews";
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream()
                .anyMatch(a -> {
                    String authority = a.getAuthority();
                    boolean matches = authority.equals("ADMIN");

                    if (matches) {
                        log.debug("✅ Найдена административная роль: {}", authority);
                    }

                    return matches;
                });

        if (isAdmin) {
            log.debug("Админ {} получает доступ к админ-панели отзывов",
                    authentication.getName());
            return "admin::reviews";
        }

        log.trace("Пользователь {} получает публичный доступ к отзывам",
                authentication.getName());
        return "reviews::fragment-reviews";
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
