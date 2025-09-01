package ru.tattoo.maxsim.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.model.Commits;
import ru.tattoo.maxsim.model.DTO.CommitsDTO;
import ru.tattoo.maxsim.repository.CommitsRepository;
import ru.tattoo.maxsim.service.interf.BlogService;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.CommitsService;
import ru.tattoo.maxsim.service.interf.SketchesService;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.stream.Collectors;

@Controller
@RequestMapping(CommitsController.URL)
public class CommitsController extends CRUDController<Commits, Long> {

    public static final String URL = "/commits";

    @Autowired
    private SketchesService sketchesService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommitsService commitsService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    String getEntityName() {
        return "admin::commit";
    }

    @Override
    CRUDService<Commits, Long> getService() {
        return commitsService;
    }

    @Override
    void updateSection(Model model) {
        model.addAttribute("commits", commitsService.findAll().stream()
                .map(commits -> modelMapper.map(commits, CommitsDTO.class)).collect(Collectors.toList()));
    }


    @PostMapping("/commits-import")
    public String upload(@ModelAttribute("commitsEntity") Commits commits, Principal principal, Model model) throws IOException, ParseException {
        commitsService.saveCommit(commits.getComment(),principal.getName());
        model.addAttribute("commits", commitsService.findLimit());
        model.addAttribute("sketches", sketchesService.findLimit());
        model.addAttribute("interestingWorks", blogService.findLimit());
        model.addAttribute("blog", blogService.findAll());
        model.addAttribute("commitsEntity", new Commits());
        return "blog::fragment-commits";
    }
    @Override
    @GetMapping("/delete/{id}")
    public String deleteCarouselImage(@PathVariable("id") Long id, Model model) throws IOException, ParseException {

        commitsService.deleteById(id);

        updateSection(model);
        return getEntityName();
    }
}
