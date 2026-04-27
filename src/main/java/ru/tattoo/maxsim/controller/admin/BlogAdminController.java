package ru.tattoo.maxsim.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.model.Commits;
import ru.tattoo.maxsim.service.interf.BlogService;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.CommitsService;

import java.io.IOException;
import java.text.ParseException;


@Controller
@Slf4j
@RequestMapping("/admin/blog")
public class BlogAdminController extends CRUDController<Blog, Long> {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommitsService commitsService;

    @Override
    protected String getEntityName() {
        return "fragment-admin";
    }

    @Override
    protected CRUDService<Blog, Long> getService() {
        return blogService;
    }

    @Override
    protected void updateSection(Model model) {
        model.addAttribute("commits", commitsService.findLimit());
        model.addAttribute("interestingWorks", blogService.findLimit());
        model.addAttribute("blog", blogService.findAll());
        model.addAttribute("commitsEntity", new Commits());
        model.addAttribute("blogEntity", new Blog());
    }


    @GetMapping()

    private String getBlogFragment(Model model, HttpServletRequest request) {
        log.info("Получено page {}",
                request.getRequestURL());
        updateSection(model);

        return getEntityName()+"::blog";
    }

    @GetMapping("/delete-commit/{id}")
    public String deleteCommit(@PathVariable("id") Long id,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model,
                               HttpServletRequest request) throws IOException, ParseException {

        commitsService.deleteById(id);
        updateSection(model);

        return getEntityName() + "::" + fragmentName;
    }



}
