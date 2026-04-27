package ru.tattoo.maxsim.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.controller.client.SketchesController;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.util.PageSize;

@Controller
@Slf4j
@RequestMapping("/admin/sketches")
public class SketchersAdminController  extends CRUDController<Sketches, Long> {

    public static final String PAGE_FRAGMENT = "fragment-admin";
    private static final int PAGE_NUMBER = 0;

    @Autowired
    private SketchesService sketchesService;

    @Override
    protected String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<Sketches, Long> getService() {
        return sketchesService;
    }

    @Override
    protected void updateSection(Model model) {
        model.addAttribute("sketchesEntity", new Sketches());
        model.addAttribute("sketches", sketchesService.getSketchesDto(null,null, PageSize.IMG_9.getPageSize(),PAGE_NUMBER));
    }

    @GetMapping()

    private String getSketchesFragment(Model model, HttpServletRequest request) {
        log.info("Получено page {}",
                request.getRequestURL());

        updateSection(model);

        return getEntityName()+"::sketches";
    }

    @RequestMapping(value = "/{page}/{number}", method = RequestMethod.GET)
    private String goToPageSketches(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        log.info("Получено page {} number {} number {}", page, number,request);

        model.addAttribute("sketchesEntity", new Sketches());
        model.addAttribute("sketches", sketchesService.getSketchesDto(null,null,number,page));
        return getEntityName()+"::sketches";
    }


}
