package ru.tattoo.maxsim.controller;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tattoo.maxsim.repository.CommitsRepository;
import ru.tattoo.maxsim.service.interf.CommitsService;
import ru.tattoo.maxsim.service.interf.InterestingWorksService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.SketchesService;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDateTime;

@Controller
public class BlogController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CommitsService commitsService;
    @Autowired
    private CommitsRepository commitsRepository;
    @Autowired
    private SketchesService sketchesService;
    @Autowired
    private InterestingWorksService interestingWorksService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping ("/blog")
    public  String blog (Model model){
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        model.addAttribute("commits", commitsService.findLimit());
        model.addAttribute("sketches", sketchesService.findLimit());
        model.addAttribute("interestingWorks", interestingWorksService.findLimit());
        return "blog";
    }
    @PostMapping("/commits-import")
    public String reviewsImport(@RequestParam("Comment") String Comment, Principal principal, Model model) throws IOException, ParseException {
        commitsService.saveImd(Comment,principal.getName());
        model.addAttribute("commits", commitsService.findLimit());
        return "blog::fragment-commits";
    }
}
