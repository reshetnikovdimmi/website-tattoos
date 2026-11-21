package ru.tattoo.maxsim.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.SketchesService;

@Controller
@Slf4j
@RequestMapping("/admin/sketches")
public class SketchersAdminController extends CRUDController<Sketches, Long> {

    public static final String PAGE_FRAGMENT = "fragment-admin/sketches";
    private static final String GRID_FRAGMENT = "fragment-admin/sketches::sketchesGridContainer";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 9;

    @Autowired
    private SketchesService sketchesService;

    @Override
    protected String getFragmentName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<Sketches, Long> getService() {
        return sketchesService;
    }

    @Override
    protected void updateSection(Model model) {
        PageResult<Sketches> sketches = sketchesService.getSketchesPage(DEFAULT_PAGE, DEFAULT_SIZE);
        model.addAttribute("sketches", sketches);
        model.addAttribute("sketchesPage", sketches);
        model.addAttribute("sketchesEntity", new Sketches());
    }

    @GetMapping({"", "/"})
    public String getSketchesFragment(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "9") int size,
                                      @RequestParam(required = false) String fragment,
                                      Model model) {
        PageResult<Sketches> sketches = sketchesService.getSketchesPage(page, size);
        model.addAttribute("sketches", sketches);
        model.addAttribute("sketchesPage", sketches);
        model.addAttribute("sketchesEntity", new Sketches());

        if ("grid".equalsIgnoreCase(fragment)) {
            return GRID_FRAGMENT;
        }
        return getFragmentName() + "::sketches";
    }

    /**
     * Старый AJAX-URL пагинации. Сохранён на время миграции JS.
     */
    @Deprecated
    @GetMapping("/{page}/{number}")
    public String goToPageSketches(@PathVariable("page") int page,
                                   @PathVariable("number") int size,
                                   Model model) {
        PageResult<Sketches> sketches = sketchesService.getSketchesPage(page, size);
        model.addAttribute("sketches", sketches);
        model.addAttribute("sketchesPage", sketches);
        model.addAttribute("sketchesEntity", new Sketches());
        return GRID_FRAGMENT;
    }
}
