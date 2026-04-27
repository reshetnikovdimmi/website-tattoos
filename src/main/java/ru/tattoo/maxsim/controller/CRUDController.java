package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
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

    protected abstract String getEntityName();
    protected abstract CRUDService<E, K> getService();
    protected abstract void updateSection(Model model);



    @GetMapping("/delete-section/{id}")
    public String deleteEntity(@PathVariable("id") K id,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model,
                               HttpServletRequest request) throws IOException, ParseException {

        getService().deleteById(id);
        updateSection(model);

        return getEntityName() + "::" + fragmentName;
    }

    @PostMapping("/image-import")
    public String uploadImage(@ModelAttribute() E object,
                              @RequestParam("file") MultipartFile fileImport,
                              @RequestParam(value = "fragment", required = false) String fragmentName,
                              Model model) throws IOException, ParseException {

        String fileName = fileImport != null ? fileImport.getOriginalFilename() : "null";
        long fileSize = fileImport != null ? fileImport.getSize() : 0;


        log.info("Файл: '{}', Размер: {} байт, Тип: {}",
                fileName, fileSize, fileImport != null ? fileImport.getContentType() : "unknown");

        log.debug("Детали объекта до обработки: {}", object != null ? object.toString() : "null");

        getService().saveImg(fileImport, object);

        updateSection(model);
        System.out.println(fragmentName);
        return getEntityName() + "::" + fragmentName;
    }

    @PostMapping("/import")
    public String createEntity(@ModelAttribute() E object,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model) throws IOException, ParseException {

        log.info("object={}",object);

        getService().create(object);
        updateSection(model);

        return getEntityName() + "::" + fragmentName;
    }
}

