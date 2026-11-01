package ru.tattoo.maxsim.controller.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    protected String getFragmentName() {
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

        return getFragmentName();
    }

    // H2 FIX: Пользователи могут создавать отзывы, но userName берётся из Principal, а не из формы
    @Override
    @PostMapping("/import")
    public String createEntity(@ModelAttribute() ReviewsUser object,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model) throws IOException, ParseException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Игнорируем userName из формы, всегда используем текущего пользователя
        if (authentication != null && authentication.isAuthenticated()) {
            object.setUserName(authentication.getName());
            log.info("Создание отзыва от имени: {}", object.getUserName());
        } else {
            log.warn("Попытка создания отзыва без авторизации");
            return "redirect:/login";
        }

        getService().create(object);
        updateSection(model);

        return getFragmentName() + "::" + fragmentName;
    }

    // H2 FIX: Удаление только своего отзыва — проверка по userName
    @Override
    @PostMapping("/delete-section/{id}")
    public String deleteEntity(@PathVariable("id") Long id,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Попытка удаления отзыва без авторизации");
            return "redirect:/login";
        }

        ReviewsUser review = reviewService.findById(id);
        if (review == null) {
            log.warn("Отзыв не найден: {}", id);
            return "redirect:/reviews";
        }

        if (!review.getUserName().equals(authentication.getName())) {
            log.warn("Попытка удаления чужого отзыва: user={}, review={}", authentication.getName(), review.getUserName());
            return "redirect:/reviews";
        }

        getService().deleteById(id);
        updateSection(model);

        return getFragmentName() + "::" + fragmentName;
    }

    // H2 FIX: Изменение только своего отзыва — проверка по userName
    @Override
    @PostMapping("/image-import")
    public String uploadImage(@ModelAttribute("entity") ReviewsUser object,
                              @RequestParam("file") org.springframework.web.multipart.MultipartFile fileImport,
                              @RequestParam(value = "fragment", required = false) String fragmentName,
                              Model model) throws IOException, ParseException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Попытка загрузки изображения без авторизации");
            return "redirect:/login";
        }

        ReviewsUser review = reviewService.findById(object.getId());
        if (review == null) {
            log.warn("Отзыв не найден для загрузки: {}", object.getId());
            return "redirect:/reviews";
        }

        if (!review.getUserName().equals(authentication.getName())) {
            log.warn("Попытка изменения чужого отзыва: user={}, review={}", authentication.getName(), review.getUserName());
            return "redirect:/reviews";
        }

        log.info("Загрузка изображения для отзыва {}: файл '{}', размер {} байт",
                object.getId(), fileImport.getOriginalFilename(), fileImport.getSize());

        getService().saveImg(fileImport, object);
        updateSection(model);

        return getFragmentName() + "::" + fragmentName;
    }


}
