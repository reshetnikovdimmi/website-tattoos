package ru.tattoo.maxsim.controller;


import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.*;
import ru.tattoo.maxsim.repository.CommitsRepository;
import ru.tattoo.maxsim.service.interf.*;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;

@Controller
@Slf4j
@RequestMapping(BlogController.URL)
public class BlogController extends CRUDController<Blog, Long> {

    public static final String URL = "/blog";

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CommitsService commitsService;
    @Autowired
    private CommitsRepository commitsRepository;
    @Autowired
    private SketchesService sketchesService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private ModelMapper modelMapper;
    private Model model;

    @Override
    String getEntityName() {
        return "admin::blog-pic";
    }

    @Override
    CRUDService<Blog, Long> getService() {
        return blogService;
    }

    @Override
    void updateSection(Model model) {
        model.addAttribute("blog", blogService.findAll());
    }

    @GetMapping()
    public String blog(Model model) {
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        model.addAttribute("commits", commitsService.findLimit());
        model.addAttribute("sketches", sketchesService.findLimit());
        model.addAttribute("interestingWorks", blogService.findLimit());
        model.addAttribute("blog", blogService.findAll());
        model.addAttribute("commitsEntity", new Commits());
        return "blog";
    }



    @Override
    @PostMapping("/import")
    public String createEntity(@ModelAttribute("hero") Blog object,
                               Model model) throws IOException, ParseException {

        getService().create(object);
        updateSection(model);
        return "admin::blog-single-text"; // Прямо указываем нужный фрагмент
    }
    
    @PostMapping("/work-import")
    public String uploadWork(@ModelAttribute("hero") Blog object,
                             @RequestParam("file") MultipartFile fileImport,
                             Model model) throws IOException, ParseException {
        object.setDate(new Date());
        getService().saveImg(fileImport, object);
        model.addAttribute("interestingWorks", blogService.findDescription());
        model.addAttribute("blogEntity", new Blog());
        return "admin::interesting-works";
    }

    @Override
    @GetMapping("/delete-work/{id}")
    public String deleteEntity(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        getService().deleteById(id);
        model.addAttribute("interestingWorks", blogService.findDescription());
        model.addAttribute("blogEntity", new Blog());
        return "admin::interesting-works";
    }

}
