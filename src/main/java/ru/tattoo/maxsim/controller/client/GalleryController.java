package ru.tattoo.maxsim.controller.client;


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
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.PageSize;

import java.security.Principal;

@Controller
@Slf4j
@RequestMapping(GalleryController.URL)
public class GalleryController {

    public static final String URL = "/gallery";
    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;


    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ClassesSectionService classesSectionService;


    protected String getEntityName() {
        return "gallery";
    }


    protected CRUDService<Images, Long> getService() {
        return imagesService;
    }

    @GetMapping()
    public String gallery(Model model) {
        GalleryDTO galleryData = prepareGalleryData(null, PAGE_NUMBER, PageSize.IMG_9.getPageSize());

        // 🔍 ЛОГИРОВАНИЕ ДЛЯ ОТЛАДКИ
        log.info("=== ОТЛАДКА GALLERY ===");
        log.info("galleryData class: {}", galleryData != null ? galleryData.getClass().getName() : "null");
        log.info("galleryData: {}", galleryData);
        log.info("galleryData.galleryImg: {}", galleryData != null ? galleryData.getGalleryImg() : "null");

       model.addAttribute("gallery", galleryData);
       model.addAttribute("classes", classesSectionService.findAll());

        return getEntityName();
    }


    @RequestMapping(value = "{style}/{page}/{number}", method = RequestMethod.GET)
    private String gallerySearch(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model, HttpServletRequest request,Principal principal) {

        log.info("Получено page {} number {}",
                page, number);
        model.addAttribute("gallery", prepareGalleryData( style, page, number));
        model.addAttribute("images", new Images());

        return getEntityName()+"::galleryFilter";
    }

    @RequestMapping(value ="/filter/style/{style}", method = RequestMethod.GET)
    public String galleryFilter(@PathVariable("style") String style, Model model) {
       
        model.addAttribute("gallery", prepareGalleryData(style, PAGE_NUMBER, PageSize.IMG_9.getPageSize()));
        model.addAttribute("classes", classesSectionService.findAll());
        return getEntityName();
    }


    @RequestMapping(value = "reviews/{page}/{number}", method = RequestMethod.GET)
    private String reviewsModal(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("gallery", prepareGalleryData(null, page, number));
        model.addAttribute("images", new Images());

        return "fragments::modal-img";
    }



    protected void updateSection(Model model) {
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
