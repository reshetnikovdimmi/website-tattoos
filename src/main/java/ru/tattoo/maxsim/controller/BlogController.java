package ru.tattoo.maxsim.controller;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.repository.CommitsRepository;
import ru.tattoo.maxsim.service.interf.*;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDateTime;

@Controller

public class BlogController extends CRUDController  {

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

    @Override
    String getEntityName() {
        return "";
    }

    @Override
    CRUDService getService() {
        return blogService;
    }

    @GetMapping ("/blog")
    public  String blog (Model model){
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        model.addAttribute("commits", commitsService.findLimit());
        model.addAttribute("sketches", sketchesService.findLimit());
        model.addAttribute("interestingWorks", blogService.findLimit());
        model.addAttribute("blog", blogService.findAll());

        return "blog";
    }

    @PostMapping("/commits-import")
    public String reviewsImport(@RequestParam("Comment") String Comment, Principal principal, Model model) throws IOException, ParseException {
        commitsService.saveCommit(Comment,principal.getName());
        model.addAttribute("commits", commitsService.findLimit());
        return "blog::fragment-commits";
    }

    @PostMapping("/update-text/{section}")
    public String uploadText(@PathVariable("section") String section, @RequestBody Blog blog, Model model) {
        blog.setSection(section);
        getService().create(blog);
        model.addAttribute("blog", blogService.findAll());
        return "admin::blog-single-text";
    }

    @PostMapping("/blog/upload-image/{section}")
    public String uploadHome(@RequestParam("image") MultipartFile fileImport, @RequestParam("id") Long id,
                             @PathVariable("section") String section,
                             Model model) throws IOException, ParseException {

        blogService.saveImg(fileImport, section, id);
        model.addAttribute("blog", blogService.findAll());
        return "admin::blog-pic";
    }
}
