package ru.tattoo.maxsim.controller.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.ImagesService;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(GalleryController.URL)
public class GalleryController {

    public static final String URL = "/gallery";
    private static final String GRID_FRAGMENT = "gallery::galleryFilter";

    private final ImagesService imagesService;
    private final ClassesSectionService classesSectionService;

    @GetMapping({"", "/"})
    public String gallery(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "9") int size,
                          @RequestParam(required = false) String style,
                          @RequestParam(required = false) String fragment,
                          Model model) {
        GalleryQuery query = GalleryQuery.of(page, size, style);
        PageResult<Images> galleryData = imagesService.getGalleryPage(query, null);

        model.addAttribute("gallery", galleryData);
        model.addAttribute("galleryPage", galleryData);
        model.addAttribute("selectedStyle", query.selectedStyle());

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
        return gallery(GalleryQuery.DEFAULT_PAGE, GalleryQuery.DEFAULT_SIZE, style, null, model);
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
        GalleryQuery query = GalleryQuery.of(page, size, style);
        model.addAttribute("gallery", imagesService.getGalleryPage(query, null));
        model.addAttribute("selectedStyle", query.selectedStyle());
        model.addAttribute("images", new Images());
        return GRID_FRAGMENT;
    }

    @Deprecated
    @GetMapping("/reviews/{page}/{number}")
    public String reviewsModal(@PathVariable("page") int page,
                               @PathVariable("number") int size,
                               Model model) {
        GalleryQuery query = GalleryQuery.of(page, size, null);
        model.addAttribute("gallery", imagesService.getGalleryPage(query, null));
        model.addAttribute("images", new Images());
        return "fragments/modals/image-picker :: image-picker";
    }
}
