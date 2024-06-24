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
import ru.tattoo.maxsim.repository.ImagesRepository;
import java.util.Collections;
import java.util.List;

@Controller
public class GalleryController {
    @Autowired
    private ImagesRepository imagesRepository;

    @GetMapping("/gallery")
    public String gallery(Model model) {
        Pageable p = PageRequest.of(0,9);

       Page<Images> images = imagesRepository.findAll(p);
        model.addAttribute("number", 9);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", 0);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("images", pageList(images));
        model.addAttribute("options", Lists.newArrayList(9, 12, 15));
        return "gallery";
    }



    @RequestMapping(value = "/gallery/{style}/{page}/{number}", method = RequestMethod.GET)

    private String gallerySearch(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {
        Page<Images> images;
        Pageable p = PageRequest.of(page,number);
        if (style.equals("Вся галерея")){
            images = imagesRepository.findAll(p);
        }else{
            images = imagesRepository.findByCategory(style,p);
        }
        model.addAttribute("images",pageList(images) );
        model.addAttribute("number", number);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("options", Lists.newArrayList(9, 12, 15));

        return "gallery::galleryFilter";
    }

    private Object pageList(Page<Images> images) {
        List<Images> objects = images.hasContent() ? images.getContent() : Collections.emptyList();
        List<List<Images>> smallerLists = Lists.partition(objects, 3);
        return smallerLists;
    }

}
