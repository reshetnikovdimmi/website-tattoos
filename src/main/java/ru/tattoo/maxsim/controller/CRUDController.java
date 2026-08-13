package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.exceptions.FileUploadException;
import ru.tattoo.maxsim.service.interf.CRUDService;

import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

@Controller
@Slf4j
public abstract class CRUDController<E, K>  {

    protected abstract String getFragmentName();
    protected abstract CRUDService<E, K> getService();
    protected abstract void updateSection(Model model);



    @GetMapping("/delete-section/{id}")
    public String deleteEntity(@PathVariable("id") K id,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model) throws IOException {

        log.debug("Удаление {} с id: {}, fragment: {}", getFragmentName(), id, fragmentName);

        getService().deleteById(id);
        updateSection(model);

        return getFragmentName() + "::" + fragmentName;
    }

    @PostMapping("/image-import")
    public String uploadImage(@ModelAttribute("entity") E object,
                              @RequestParam("file") MultipartFile fileImport,
                              @RequestParam(value = "fragment", required = false) String fragmentName,
                              Model model) throws IOException, ParseException, FileUploadException {

        log.info("Загрузка изображения для {}: файл '{}', размер {} байт",
                fragmentName, fileImport.getOriginalFilename(), fileImport.getSize());
        log.debug("Объект до сохранения: {}", object);

        getService().saveImg(fileImport, object);
        updateSection(model);

        return getFragmentName() + "::" + fragmentName;
    }

    @PostMapping("/import")
    public String createEntity(@ModelAttribute("entity") E object,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model) throws IOException, ParseException {

        log.info("Сохранение {}: {}", fragmentName, object);

        getService().create(object);
        updateSection(model);

        return getFragmentName() + "::" + fragmentName;
    }
}

