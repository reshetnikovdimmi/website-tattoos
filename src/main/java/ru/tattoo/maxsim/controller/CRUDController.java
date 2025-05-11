package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HomeService;

import java.io.IOException;
import java.text.ParseException;

@Controller
public abstract class CRUDController<E, K>  {

    abstract String getEntityName();
    abstract CRUDService<E, K> getService();

@Autowired
private HomeService homeService;

    @GetMapping("/delete-section/{id}")
    public String deleteCarouselImage(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        getService().deleteImg(id);
        updateCarousel(model);
        return getEntityName();
    }

    @PostMapping("/home-import")
    public String uploadHome(@RequestParam("file") MultipartFile fileImport,
                             @RequestParam("textH1") String textH1,
                             @RequestParam("textH2") String textH2,
                             @RequestParam("textH3") String textH3,
                             Model model) throws IOException, ParseException {
        getService().saveImg(fileImport, textH1,textH2,textH3);
        updateCarousel(model);
        return getEntityName();
    }

    private void updateCarousel(Model model) {
        model.addAttribute("home", homeService.findAll());
    }
}
