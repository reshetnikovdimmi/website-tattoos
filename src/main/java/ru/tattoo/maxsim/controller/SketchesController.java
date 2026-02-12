package ru.tattoo.maxsim.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.util.PageSize;


@Controller
@Slf4j
@RequestMapping(SketchesController.URL)
public class SketchesController extends CRUDController<Sketches, Long> {

    public static final String URL = "/sketches";
    private static final int PAGE_NUMBER = 0;

    @Autowired
    private SketchesService sketchesService;

    @Override
    String getEntityName() {
        return "fragment-admin::sketches-import";
    }

    @Override
    CRUDService<Sketches, Long> getService() {
        return sketchesService;
    }

    @GetMapping()
    public String sketches(Model model) {

        model.addAttribute("sketches", sketchesService.getSketchesDto(null,null,PageSize.IMG_9.getPageSize(),PAGE_NUMBER));

        return "sketches";
    }

    @GetMapping("/admin")

    private String getSketchesFragment(Model model, HttpServletRequest request) {
        log.info("Получено page {}",
                request.getRequestURL());

        model.addAttribute("sketchesEntity", new Sketches());
        model.addAttribute("sketches", sketchesService.getSketchesDto(null,null,PageSize.IMG_9.getPageSize(),PAGE_NUMBER));

        return "fragment-admin::sketches";
    }

    @RequestMapping(value = "/{page}/{number}", method = RequestMethod.GET)
    private String remainsGroupShop(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        model.addAttribute("sketches", sketchesService.getSketchesDto(null,null,number,page));

        return "sketches::galleryFilter";
    }

    @RequestMapping(value = "/admin/{page}/{number}", method = RequestMethod.GET)
    private String goToPageSketches(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {
        model.addAttribute("sketchesEntity", new Sketches());
        model.addAttribute("sketches", sketchesService.getSketchesDto(null,null,number,page));
        return getEntityName();
    }




    @Override
    void updateSection(Model model) {
        model.addAttribute("sketchesEntity", new Sketches());
        model.addAttribute("sketches", sketchesService.getSketchesDto(null,null,PageSize.IMG_9.getPageSize(),PAGE_NUMBER));
    }
}
