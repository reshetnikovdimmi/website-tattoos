package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.PageSize;


@Controller
public class GalleryController {

    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;


    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ImagesRepository imagesRepository;

    @GetMapping("/gallery")
    public String gallery(Model model) {

        model.addAttribute("gallery", imagesService.getGalleryDto(null,null,PageSize.IMG_9.getPageSize(),PAGE_NUMBER));

        return "gallery";
    }

    @RequestMapping(value = "/gallery/{style}/{page}/{number}", method = RequestMethod.GET)
    private String gallerySearch(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("gallery", imagesService.getGalleryDto(style.equals(ALL_GALLERY) ?null:style,null, number,page));

        return "gallery::galleryFilter";
    }

    @RequestMapping(value = "/gallery/reviews/{page}/{number}", method = RequestMethod.GET)
    private String reviewsModal(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("gallery", imagesService.getGalleryDto(null,null,number,page));

        return "fragments::modal-img";
    }
}
