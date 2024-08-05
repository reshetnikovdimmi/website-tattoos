package ru.tattoo.maxsim.controller;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.SketchesRepository;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.util.PageSize;

import java.util.Collections;
import java.util.List;

@Controller
public class SketchesController {

    private static final int PAGE_NUMBER = 0;

    @Autowired
    private SketchesService sketchesService;

    @GetMapping("/sketches")
    public String sketches(Model model) {

        Page<Sketches> images = sketchesService.partition(PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize()));
        model.addAttribute("number", PageSize.IMG_9.getPageSize());
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("images", sketchesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());
        return "sketches";
    }

    @RequestMapping(value = "/sketches/{page}/{number}", method = RequestMethod.GET)
    private String remainsGroupShop(@PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        Page<Sketches> images = sketchesService.partition(PageRequest.of(page,number));
        model.addAttribute("images",sketchesService.pageList(images) );
        model.addAttribute("number", number);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("options", PageSize.getLisPageSize());
        return "sketches::galleryFilter";
    }


}
