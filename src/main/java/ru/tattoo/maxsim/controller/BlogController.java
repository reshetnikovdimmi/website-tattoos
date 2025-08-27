package ru.tattoo.maxsim.controller;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.repository.CommitsRepository;
import ru.tattoo.maxsim.service.interf.*;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDateTime;

@Controller
@RequestMapping(BlogController.URL)
public class BlogController extends CRUDController<Blog, Long>  {

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

    @Override
    String getEntityName() {
        return "admin::blog-pic";
    }

    @Override
    CRUDService getService() {
        return blogService;
    }

    @Override
    void updateSection(Model model) {
        model.addAttribute("blog", blogService.findAll());
    }

    @GetMapping ()
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

    @Override
    protected Blog prepareObject(MultipartFile fileImport, Blog blog) throws IOException {
        blog.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        ImageUtils.saveImage(fileImport, blog.getImageName());
        return blog;
    }
    @Override
    @PostMapping("/import")
    public String upload(@ModelAttribute("hero") Blog object,
                         Model model) throws IOException, ParseException {
        getService().create(object);
        updateSection(model);
        return "admin::blog-single-text";
    }

}
