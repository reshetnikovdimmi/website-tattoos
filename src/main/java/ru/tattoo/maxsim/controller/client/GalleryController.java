package ru.tattoo.maxsim.controller.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.ImagesService;

@Controller
@Slf4j
@RequestMapping(GalleryController.URL)
public class GalleryController {

    public static final String URL = "/gallery";
    private static final String ALL_GALLERY = "Вся галерея";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 9;
    private static final String GRID_FRAGMENT = "gallery::galleryFilter";

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ClassesSectionService classesSectionService;

    @GetMapping({"", "/"})
    public String gallery(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "9") int size,
                          @RequestParam(required = false) String style,
                          @RequestParam(required = false) String fragment,
                          Model model) {
        PageResult<Images> galleryData = prepareGalleryData(style, page, size);
        model.addAttribute("gallery", galleryData);
        model.addAttribute("galleryPage", galleryData);
        model.addAttribute("selectedStyle", style == null ? "" : style);

        if ("grid".equalsIgnoreCase(fragment)) {
            return GRID_FRAGMENT;
        }

        model.addAttribute("classes", classesSectionService.findAll());
        return "gallery";
    }

    /**
     * Старый URL фильтра сохранён для обратной совместимости ссылок.
     */
    @Deprecated
    @GetMapping("/filter/style/{style}")
    public String galleryFilter(@PathVariable("style") String style, Model model) {
        return gallery(DEFAULT_PAGE, DEFAULT_SIZE, style, null, model);
    }

    /**
     * Старый AJAX-URL пагинации/фильтра. Возвращает только grid-фрагмент.
     */
    @Deprecated
    @GetMapping("/{style}/{page}/{number}")
    public String gallerySearch(@PathVariable("style") String style,
                                @PathVariable("page") int page,
                                @PathVariable("number") int size,
                                Model model) {
        model.addAttribute("gallery", prepareGalleryData(style, page, size));
        model.addAttribute("selectedStyle", style);
        model.addAttribute("images", new Images());
        return GRID_FRAGMENT;
    }

    @Deprecated
    @GetMapping("/reviews/{page}/{number}")
    public String reviewsModal(@PathVariable("page") int page,
                               @PathVariable("number") int size,
                               Model model) {
        model.addAttribute("gallery", prepareGalleryData(null, page, size));
        model.addAttribute("images", new Images());
        return "fragments::modal-img";
    }

    private PageResult<Images> prepareGalleryData(String style, int page, int size) {
        String normalizedStyle = ALL_GALLERY.equals(style) ? null : style;
        return imagesService.getGalleryPage(normalizedStyle, null, page, size);
    }
}
