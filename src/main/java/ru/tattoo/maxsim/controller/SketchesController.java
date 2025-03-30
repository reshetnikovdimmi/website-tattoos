package ru.tattoo.maxsim.controller;


import jakarta.servlet.http.HttpServletRequest;
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
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.util.PageSize;


@Controller
public class SketchesController {

    private static final int PAGE_NUMBER = 0;

    @Autowired
    private SketchesService sketchesService;

    @GetMapping("/sketches")
    public String sketches(Model model) {

        model.addAttribute("sketches", sketchesService.pageList(null,null,PageSize.IMG_9.getPageSize(),PAGE_NUMBER));

        return "sketches";
    }

    @RequestMapping(value = "/sketches/{page}/{number}", method = RequestMethod.GET)
    private String remainsGroupShop(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("sketches", sketchesService.pageList(null,null,number,page));

        return "sketches::galleryFilter";
    }


}
