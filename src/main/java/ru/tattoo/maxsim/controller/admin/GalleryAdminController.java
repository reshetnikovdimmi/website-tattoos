package ru.tattoo.maxsim.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.ImagesService;

@Controller
@RequestMapping("/admin/gallery")
@Slf4j
public class GalleryAdminController extends CRUDController<Images, Long> {

    public static final String PAGE_FRAGMENT = "fragment-admin/gallery";
    private static final String GRID_FRAGMENT = "fragment-admin/gallery::galleryGridContainer";
    private static final String ALL_GALLERY = "Вся галерея";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 9;

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ClassesSectionService classesSectionService;

    @Override
    protected String getFragmentName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<Images, Long> getService() {
        return imagesService;
    }

    @Override
    protected void updateSection(Model model) {
        PageResult<Images> gallery = prepareGalleryData(null, DEFAULT_PAGE, DEFAULT_SIZE);
        model.addAttribute("gallery", gallery);
        model.addAttribute("galleryPage", gallery);
        model.addAttribute("selectedStyle", "");
        model.addAttribute("images", new Images());
        model.addAttribute("styleList", classesSectionService.findAll());
    }

    @GetMapping({"", "/"})
    public String getGalleryFragment(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "9") int size,
                                     @RequestParam(required = false) String style,
                                     @RequestParam(required = false) String fragment,
                                     Model model) {
        PageResult<Images> gallery = prepareGalleryData(style, page, size);
        model.addAttribute("gallery", gallery);
        model.addAttribute("galleryPage", gallery);
        model.addAttribute("selectedStyle", style == null ? "" : style);
        model.addAttribute("images", new Images());
        model.addAttribute("styleList", classesSectionService.findAll());

        if ("grid".equalsIgnoreCase(fragment)) {
            return GRID_FRAGMENT;
        }
        return getFragmentName() + "::gallery";
    }

    /**
     * Старый AJAX-URL фильтра/пагинации. Сохранён на время миграции JS.
     */
    @Deprecated
    @GetMapping("/{style}/{page}/{number}")
    public String gallerySearch(@PathVariable("style") String style,
                                @PathVariable("page") int page,
                                @PathVariable("number") int size,
                                Model model) {
        PageResult<Images> gallery = prepareGalleryData(style, page, size);
        model.addAttribute("gallery", gallery);
        model.addAttribute("galleryPage", gallery);
        model.addAttribute("selectedStyle", style == null ? "" : style);
        model.addAttribute("images", new Images());
        model.addAttribute("styleList", classesSectionService.findAll());
        return GRID_FRAGMENT;
    }

    @PostMapping("/toggle-featured")
    public String updateFlag(@RequestParam("id") Long id,
                             @RequestParam("flag") boolean flag,
                             Model model,
                             Authentication auth) {
        String message = imagesService.updateImageFlag(id, flag);
        model.addAttribute("message", "Флаг обновлен успешно:" + "-" + message);
        log.info("Обновление флага: id={}, flag={}, user={}", id, flag, auth.getName());
        return "fragments/modals/message :: message-body";
    }

    private PageResult<Images> prepareGalleryData(String style, int page, int size) {
        String normalizedStyle = ALL_GALLERY.equals(style) ? null : style;
        return imagesService.getGalleryPage(normalizedStyle, null, page, size);
    }
}
