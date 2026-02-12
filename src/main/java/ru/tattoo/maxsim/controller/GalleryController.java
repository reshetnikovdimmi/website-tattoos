package ru.tattoo.maxsim.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.DTO.GalleryDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.PageSize;

import java.security.Principal;

@Controller
@Slf4j
@RequestMapping(GalleryController.URL)
public class GalleryController extends CRUDController<Images, Long> {

    public static final String URL = "/gallery";
    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;


    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ImagesRepository imagesRepository;

    @Override
    String getEntityName() {
        return "fragment-admin::img-import";
    }

    @Override
    CRUDService<Images, Long> getService() {
        return imagesService;
    }

    @GetMapping()
    public String gallery(Model model) {

        model.addAttribute("gallery", prepareGalleryData(null, PAGE_NUMBER, PageSize.IMG_9.getPageSize()));

        return "gallery";
    }

    @GetMapping("/admin")

    private String getGalleryFragment(Model model, HttpServletRequest request) {
        log.info("Получено page {}",
                request.getRequestURL());
        model.addAttribute("gallery", prepareGalleryData(null, PAGE_NUMBER, PageSize.IMG_9.getPageSize()));
        model.addAttribute("images", new Images());

        return "fragment-admin::gallery";
    }

    @RequestMapping(value = "{style}/{page}/{number}", method = RequestMethod.GET)
    private String gallerySearch(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model, HttpServletRequest request,Principal principal) {

        boolean isAdmin = principal instanceof Authentication auth
                && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        log.info("Получено page {} number {}",
                page, number);
        model.addAttribute("gallery", prepareGalleryData( style, page, number));
        model.addAttribute("images", new Images());

        return isAdmin ? getEntityName() : "gallery::galleryFilter";
    }

    @RequestMapping(value = "reviews/{page}/{number}", method = RequestMethod.GET)
    private String reviewsModal(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("gallery", prepareGalleryData(null, page, number));
        model.addAttribute("images", new Images());

        return "fragments::modal-img";
    }

    @PostMapping("/update-flag")

    public String updateFlag(@RequestParam("id") Long id,
                             @RequestParam("flag") boolean flag,
                             Model model,
                             Authentication auth) {

        log.info("Обновление флага: id={}, flag={}, user={}", id, flag, auth.getName());

        try {
            String message = imagesService.updateImageFlag(id, flag);
            model.addAttribute("message", message);
            log.debug("Флаг обновлен успешно: {}", message);

        } catch (EntityNotFoundException e) {
            log.error("Изображение не найдено: id={}, error={}", id, e.getMessage());
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка обновления флага: id={}, error={}", id, e.getMessage(), e);
            model.addAttribute("error", "Внутренняя ошибка");
        }

        return "admin::modal-body";
    }

    @Override
    void updateSection(Model model) {
        model.addAttribute("gallery", prepareGalleryData(null, PAGE_NUMBER, PageSize.IMG_9.getPageSize()));
        model.addAttribute("images", new Images());
    }


    // 🔄 метод для подготовки данных галереи
    private GalleryDTO prepareGalleryData(String style, int page, int page_size) {
        // ✅ Логика обработки стиля "Вся галерея"
        String normalizedStyle = ALL_GALLERY.equals(style) ? null : style;
        return imagesService.getGalleryDto(normalizedStyle, null, page_size, page);
    }
}
