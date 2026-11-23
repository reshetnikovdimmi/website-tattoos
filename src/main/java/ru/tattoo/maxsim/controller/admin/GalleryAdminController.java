package ru.tattoo.maxsim.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.ImagesService;

@Controller
@RequestMapping("/admin/gallery")
@Slf4j
@RequiredArgsConstructor
public class GalleryAdminController extends CRUDController<Images, Long> {

    public static final String PAGE_FRAGMENT = "fragment-admin/gallery";
    private static final String GRID_FRAGMENT = "fragment-admin/gallery::galleryGridContainer";

    private final ImagesService imagesService;
    private final ClassesSectionService classesSectionService;

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
        GalleryQuery query = GalleryQuery.of(GalleryQuery.DEFAULT_PAGE, GalleryQuery.DEFAULT_SIZE, null);
        PageResult<Images> gallery = imagesService.getGalleryPage(query, null);
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
        GalleryQuery query = GalleryQuery.of(page, size, style);
        PageResult<Images> gallery = imagesService.getGalleryPage(query, null);
        model.addAttribute("gallery", gallery);
        model.addAttribute("galleryPage", gallery);
        model.addAttribute("selectedStyle", query.selectedStyle());
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
        GalleryQuery query = GalleryQuery.of(page, size, style);
        PageResult<Images> gallery = imagesService.getGalleryPage(query, null);
        model.addAttribute("gallery", gallery);
        model.addAttribute("galleryPage", gallery);
        model.addAttribute("selectedStyle", query.selectedStyle());
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
}
