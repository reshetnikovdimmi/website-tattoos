package ru.tattoo.maxsim.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tattoo.maxsim.model.Commits;
import ru.tattoo.maxsim.model.DTO.CommitsDTO;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.CommitsService;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.stream.Collectors;

@Controller
@RequestMapping(CommitsController.URL)
public class CommitsController extends CRUDController<Commits, Long> {

    public static final String URL = "/commits";

    @Autowired
    private CommitsService commitsService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    String getEntityName() {
        return "admin::reviews";
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
    public String reviewsImport(@RequestParam("Comment") String Comment, Principal principal, Model model) throws IOException, ParseException {
        commitsService.saveCommit(Comment,principal.getName());
        model.addAttribute("commits", commitsService.findLimit());
        return "blog::fragment-commits";
    }
}
