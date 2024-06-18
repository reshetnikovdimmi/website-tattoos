package ru.tattoo.maxsim.controller;

import com.google.common.collect.Lists;
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
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.SketchesRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SketchesController {
    @Autowired
    private SketchesRepository sketchesRepository;
    @GetMapping("/sketches")
    public String sketches(Model model) {
        Pageable p = PageRequest.of(0,9);

        Page<Sketches> images = sketchesRepository.findAll(p);



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

        Pageable p = PageRequest.of(page,number);
        Page<Sketches> images = sketchesRepository.findAll(p);
        model.addAttribute("images",pageList(images) );
        model.addAttribute("number", number);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("options", Lists.newArrayList(9, 12, 15));
        return "sketches::galleryFilter";
    }

    private Object pageList(Page<Sketches> images) {
        int cou=0;
        List<Sketches> img = new ArrayList<>();
        List<List<Sketches>> pageList = new ArrayList<>();
        for (Sketches i:images){
            cou++;
            img.add(i);

            if (cou==3){
                pageList.add(img);
                img = new ArrayList<>();
                cou=0;
            }
        }
        if (img!=null)pageList.add(img);
        return pageList;
    }
}
