package ru.tattoo.maxsim.controller.admin;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.DTO.GalleryDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.PageSize;

import java.security.Principal;

@Controller
@RequestMapping("/admin/gallery")
@Slf4j
public class GalleryAdminController extends CRUDController<Images, Long> {

    public static final String PAGE_FRAGMENT = "fragment-admin";
    public static final String URL = "/gallery";
    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;

    @Autowired
    private ImagesService imagesService;
    @Autowired
    private ClassesSectionService classesSectionService;

    @Override
    protected String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<Images, Long> getService() {
        return imagesService;
    }

    @Override
    protected void updateSection(Model model) {
        model.addAttribute("gallery", prepareGalleryData(null, PAGE_NUMBER, PageSize.IMG_9.getPageSize()));
        model.addAttribute("images", new Images());
        model.addAttribute("styleList", classesSectionService.findAll());
    }

    @GetMapping()
    private String getGalleryFragment(Model model,
                                      HttpServletRequest request) {
        log.info("Загрузка галереи: {}", request.getRequestURI());
        updateSection(model);
        return getEntityName()+"::galleryGridContainer";
    }

    @RequestMapping(value = "{style}/{page}/{number}", method = RequestMethod.GET)
    private String gallerySearch(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model, HttpServletRequest request, Principal principal) {

        log.info("Получено page {} number {}", page, number);

        model.addAttribute("gallery", prepareGalleryData( style, page, number));
        model.addAttribute("images", new Images());
        model.addAttribute("styleList", classesSectionService.findAll());

        return getEntityName()+"::galleryGridContainer";
    }

    @PostMapping("/toggle-featured")
    public String updateFlag(@RequestParam("id") Long id,
                             @RequestParam("flag") boolean flag,
                             Model model,
                             Authentication auth) {

        log.info("Обновление флага: id={}, flag={}, user={}", id, flag, auth.getName());

        try {
            String message = imagesService.updateImageFlag(id, flag);
            model.addAttribute("message", "Флаг обновлен успешно:"+"-" + message);
            log.debug("Флаг обновлен успешно: {}", message);

        } catch (EntityNotFoundException e) {
            log.error("Изображение не найдено: id={}, error={}", id, e.getMessage());
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка обновления флага: id={}, error={}", id, e.getMessage(), e);
            model.addAttribute("error", "Внутренняя ошибка");
        }

        return "modal::modal-body";
    }

    // 🔄 метод для подготовки данных галереи
    private GalleryDTO prepareGalleryData(String style, int page, int page_size) {
        // ✅ Логика обработки стиля "Вся галерея"

        String normalizedStyle = ALL_GALLERY.equals(style) ? null : style;

        return imagesService.getGalleryDto(normalizedStyle, null, page_size, page);
    }
}
