package ru.tattoo.maxsim.controller.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.service.interf.SketchesService;

@Controller
@Slf4j
@RequestMapping(SketchesController.URL)
public class SketchesController {

    public static final String URL = "/sketches";
    public static final String PAGE_FRAGMENT = "sketches";
    private static final String GRID_FRAGMENT = "sketches::galleryFilter";

    @Autowired
    private SketchesService sketchesService;

    @GetMapping({"", "/"})
    public String sketches(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "9") int size,
                           @RequestParam(required = false) String fragment,
                           Model model) {
        PageResult<Sketches> sketchesPage = sketchesService.getSketchesPage(page, size);
        model.addAttribute("sketches", sketchesPage);
        model.addAttribute("sketchesPage", sketchesPage);
        model.addAttribute("sketchesEntity", new Sketches());

        if ("grid".equalsIgnoreCase(fragment)) {
            return GRID_FRAGMENT;
        }

        return PAGE_FRAGMENT;
    }

    /**
     * Старый AJAX-URL пагинации (path variables) до перехода на page/size.
     */
    @Deprecated
    @GetMapping("/{page}/{number}")
    public String goToPageSketches(@PathVariable("page") int page,
                                   @PathVariable("number") int size,
                                   Model model) {
        model.addAttribute("sketches", sketchesService.getSketchesPage(page, size));
        model.addAttribute("sketchesEntity", new Sketches());
        return GRID_FRAGMENT;
    }
}
