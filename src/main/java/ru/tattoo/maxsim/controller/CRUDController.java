package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HomeService;

import java.io.IOException;
import java.text.ParseException;

@Controller
public abstract class CRUDController<E, K>  {

    abstract String getEntityName();
    abstract CRUDService<E, K> getService();

    protected E prepareObject(MultipartFile fileImport, E object) throws IOException {
        return object;
    }

    protected E prepareObject(E object) throws IOException {
        return object;
    }

    abstract void updateSection(Model model);



    @GetMapping("/delete-section/{id}")
    public String deleteCarouselImage(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        getService().deleteImg(id);
        updateSection(model);
        return getEntityName();
    }

    @PostMapping("/image-import")
    public String uploadImage(@ModelAttribute("hero") E object,
                             @RequestParam("file") MultipartFile fileImport,
                             Model model) throws IOException, ParseException {
        getService().create(prepareObject(fileImport, object));
        updateSection(model);
        return getEntityName();
    }

    @PostMapping("/import")
    public String upload(@ModelAttribute("hero") E object,
                              Model model) throws IOException, ParseException {
        getService().create(prepareObject(object));
        updateSection(model);
        return getEntityName();
    }
}
