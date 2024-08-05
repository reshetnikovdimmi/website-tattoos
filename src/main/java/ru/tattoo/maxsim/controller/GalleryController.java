package ru.tattoo.maxsim.controller;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.PageSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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

        Pageable p = PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize());
        Page<Images> images = imagesService.partition(p);

        model.addAttribute("number", PageSize.IMG_9.getPageSize());
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("images", imagesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());
        return "gallery";
    }


    @RequestMapping(value = "/gallery/{style}/{page}/{number}", method = RequestMethod.GET)

    private String gallerySearch(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        Page<Images> images;
        Pageable p = PageRequest.of(page, number);
        if (style.equals(ALL_GALLERY)) {
            images = imagesService.partition(p);
        } else {
            images = imagesService.findByCategory(style, p);
        }
        model.addAttribute("images", imagesService.pageList(images));
        model.addAttribute("number", number);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("options", PageSize.getLisPageSize());

        return "gallery::galleryFilter";
    }



}
