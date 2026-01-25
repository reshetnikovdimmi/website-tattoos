package ru.tattoo.maxsim.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.service.interf.CRUDService;

import java.io.IOException;
import java.text.ParseException;

@Controller
@Slf4j
public abstract class CRUDController<E, K>  {

    abstract String getEntityName();
    abstract CRUDService<E, K> getService();
    abstract void updateSection(Model model);



    @GetMapping("/delete-section/{id}")
    public String deleteEntity(@PathVariable("id") K id, Model model) throws IOException, ParseException {
        getService().deleteById(id);
        updateSection(model);
        return getEntityName();
    }

    @PostMapping("/image-import")
    public String uploadImage(@ModelAttribute("hero") E object,
                             @RequestParam("file") MultipartFile fileImport,
                             Model model) throws IOException, ParseException {
        getService().saveImg(fileImport, object);

        updateSection(model);
        return getEntityName();
    }

    @PostMapping("/import")
    public String createEntity(@ModelAttribute("hero") E object,
                              Model model) throws IOException, ParseException {

        getService().create(object);
        updateSection(model);
        return getEntityName();
    }
}

