package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.ImageUtils;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.text.ParseException;


@Controller
@RequestMapping(GalleryController.URL)
public class GalleryController extends CRUDController<Images, Long> {

    public static final String URL = "/gallery";
    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;


    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ImagesRepository imagesRepository;

    @GetMapping()
    public String gallery(Model model) {

        model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));

        return "gallery";
    }

    @RequestMapping(value = "/admin/{style}/{page}/{number}", method = RequestMethod.GET)
    private String galleryAdmin(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("gallery", imagesService.getGalleryDto(style.equals(ALL_GALLERY) ? null : style, null, number, page));
        updateSection(model);
        return getEntityName();
    }

    @RequestMapping(value = "{style}/{page}/{number}", method = RequestMethod.GET)
    private String gallerySearch(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("gallery", imagesService.getGalleryDto(style.equals(ALL_GALLERY) ? null : style, null, number, page));

        return "gallery::galleryFilter";
    }

    @RequestMapping(value = "reviews/{page}/{number}", method = RequestMethod.GET)
    private String reviewsModal(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("gallery", imagesService.getGalleryDto(null, null, number, page));

        return "fragments::modal-img";
    }

    @Override
    String getEntityName() {
        return "admin::img-import";
    }

    @Override
    CRUDService<Images, Long> getService() {
        return imagesService;
    }

    @Override
    void updateSection(Model model) {

        model.addAttribute("images", new Images());
    }

    @Override
    @GetMapping("/admin/delete-section/{id}")
    public String deleteCarouselImage(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        getService().deleteImg(id);
        updateSection(model);
        model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
        return getEntityName();
    }


    @Override
    @PostMapping("/image-import")
    public String uploadImage(@ModelAttribute("hero") Images object,
                              @RequestParam("file") MultipartFile fileImport,
                              Model model) throws IOException, ParseException {
        object.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        ImageUtils.saveImage(fileImport, object.getImageName());
        getService().create(object);
        model.addAttribute("gallery", imagesService.getGalleryDto(object.getCategory().equals(ALL_GALLERY) ? null : object.getCategory(), null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
        updateSection(model);
        return getEntityName();
    }

    @PostMapping("/admin/update-flag")
    public String updateFlag(@RequestParam("id") Long id, @RequestParam("flag") boolean flag,
                             Model model) {
        Images images = new Images();
        images.setId(id);
        images.setFlag(flag);

        String message = imagesService.bestImage(images) ? "Установлено" : "Снято";
        model.addAttribute("message", message);

        return "admin::modal-body";
    }
}
