package ru.tattoo.maxsim.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Collection;
import java.util.stream.Collectors;

@Controller
@Slf4j
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
        // Минимальное логирование для продакшена
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.trace("Неаутентифицированный доступ к отзывам");
            return "reviews::fragment-reviews";
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream()
                .anyMatch(a -> {
                    String authority = a.getAuthority();
                    boolean matches = authority.equals("ADMIN");

                    if (matches) {
                        log.debug("✅ Найдена административная роль: {}", authority);
                    }

                    return matches;
                });

        if (isAdmin) {
            log.debug("Админ {} получает доступ к админ-панели отзывов",
                    authentication.getName());
            return "fragment-admin::commit";
        }

        log.trace("Пользователь {} получает публичный доступ к отзывам",
                authentication.getName());
        return "blog::fragment-commits";
    }

    @Override
    CRUDService<Commits, Long> getService() {
        return commitsService;
    }

    @Override
    void updateSection(Model model) {
        model.addAttribute("commits", commitsService.findAll().stream()
                .map(commits -> modelMapper.map(commits, CommitsDTO.class)).collect(Collectors.toList()));

        model.addAttribute("sketches", sketchesService.findLimit());
        model.addAttribute("interestingWorks", blogService.findLimit());
        model.addAttribute("blog", blogService.findAll());
        model.addAttribute("commitsEntity", new Commits());
    }

}
