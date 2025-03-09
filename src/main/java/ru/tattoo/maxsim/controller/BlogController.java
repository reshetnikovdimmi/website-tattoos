package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.CommitsDTO;
import ru.tattoo.maxsim.repository.CommitsRepository;
import ru.tattoo.maxsim.repository.InterestingWorksRepository;
import ru.tattoo.maxsim.repository.SketchesRepository;
import ru.tattoo.maxsim.service.interf.CommitsService;
import ru.tattoo.maxsim.service.interf.ReviewService;

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
    private SketchesRepository sketchesRepository;
    @Autowired
    private InterestingWorksRepository interestingWorksRepository;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping ("/blog")
    public  String blog (Model model){
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("count", reviewService.getCount());
        model.addAttribute("commits", commitsRepository.findLimit().stream().map(commits->modelMapper.map(commits, CommitsDTO.class)));
        model.addAttribute("sketches", sketchesRepository.findLimit());
        model.addAttribute("interestingWorks", interestingWorksRepository.findLimit());
        return "blog";
    }
    @PostMapping("/commits-import")
    public String reviewsImport(@RequestParam("Comment") String Comment, Principal principal, Model model) throws IOException, ParseException {
        commitsService.saveImd(Comment,principal.getName());
        model.addAttribute("commits", commitsRepository.findLimit());
        return "blog::fragment-commits";
    }
}
