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
import java.util.Collections;
import java.util.List;

@Controller
public class SketchesController {

    @Autowired
    private SketchesRepository sketchesRepository;

    @GetMapping("/sketches")
    public String sketches(Model model) {

        Page<Sketches> images = sketchesRepository.findAll(PageRequest.of(0,9));
        model.addAttribute("number", 9);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", 0);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("images", pageList(images));
        model.addAttribute("options", Lists.newArrayList(9, 12, 15));
        return "sketches";
    }

    @RequestMapping(value = "/sketches/{page}/{number}", method = RequestMethod.GET)
    private String remainsGroupShop(@PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        Page<Sketches> images = sketchesRepository.findAll(PageRequest.of(page,number));
        model.addAttribute("images",pageList(images) );
        model.addAttribute("number", number);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("options", Lists.newArrayList(9, 12, 15));
        return "sketches::galleryFilter";
    }

    private Object pageList(Page<Sketches> images) {
        List<Sketches> objects = images.hasContent() ? images.getContent() : Collections.emptyList();
        //todo: magic number
        List<List<Sketches>> smallerLists = Lists.partition(objects, 3);
        return smallerLists;
    }
}
