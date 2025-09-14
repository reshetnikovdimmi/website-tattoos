package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.util.ImageUtils;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.Date;

@Controller
public class LkController {

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    private static final int PAGE_NUMBER = 0;

    /**
     * Загружает личную страницу пользователя.
     */
    @GetMapping("/lk")
    public String loadPersonalPage(Model model, Principal principal) {
        model.addAttribute("userTattoo", new Images());
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
        return "lk";
    }

    /**
     * Отображение фрагмента с эскизами работ пользователя.
     */
    @GetMapping("/sketchesrs/{page}/{number}")
    public String showSketchesFragment(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model, Principal principal) {
        loadUserGallery(model, principal, number, page); // Загружаем галерею пользователя
        return "fragments::modal-img";
    }

    /**
     * Загружает фрагмент с информацией о пользователе.
     */
    @GetMapping("/user-info")
    public String loadUserInfoFragment(Model model, Principal principal) {
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        return "fragments::review-fragment";
    }

    /**
     * Сохраняет отзыв пользователя.
     */
    @PostMapping("/reviews-user-import")
    public String saveUserReview(@ModelAttribute("reviewsEntity") ReviewsUser object, Model model, Principal principal) throws IOException, ParseException {
        object.setUserName(principal.getName());
        object.setDate(new Date());
        reviewService.create(object); // Сохраняем отзыв
        loadUserDataIntoModel(model, principal);                         // Обновляем данные пользователя
        return "fragments::review-fragment";
    }

    /**
     * Загружает новое тату пользователя.
     */
    @PostMapping("/tattoos-user-import")
    public String uploadUserTattoo(@RequestParam("file") MultipartFile fileImport,@ModelAttribute("userTattoo") Images object, Model model, Principal principal) throws IOException, ParseException {
        model.addAttribute("userTattoo", new Images());
        object.setUserName(principal.getName());
        object.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        ImageUtils.saveImage(fileImport, object.getImageName());
        imagesService.create(object); // Сохраняем изображение
        loadUserDataIntoModel(model, principal);                     // Обновляем данные пользователя
        return "fragments::first-fragment";
    }

    /**
     * Загружает форму редактирования профиля.
     */
    @GetMapping("/profile-editing")
    public String loadProfileEditForm(Model model, Principal principal) {
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        return "fragments::profile-editing";
    }
    @PostMapping("/update/profile-editing")
    public String loadProfileEditForm(@ModelAttribute("UserDTO") User object, Model model, Principal principal) {
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        return "fragments::profile-editing";
    }
    /**
     * Загружает галерею татуировок пользователя.
     */
    @GetMapping("/user-tattoos")
    public String loadUserTattoos(Model model, Principal principal) {
        model.addAttribute("userTattoo", new Images());
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        return "fragments::first-fragment";
    }

    /**
     * Загружает аватар пользователя.
     */
    @PostMapping("/avatar-import")
    public String uploadUserAvatar(@RequestParam("file") MultipartFile fileImport, Model model, Principal principal) throws IOException, ParseException {

        userService.updateUserAvatar(fileImport, principal); // Сохраняем новый аватар
        loadUserDataIntoModel(model, principal);       // Обновляем данные пользователя
        return "fragments::profile-editing";
    }

    /**
     * Загружает данные пользователя в модель.
     */
    private void loadUserDataIntoModel(Model model, Principal principal) {
        model.addAttribute("UserDTO", userService.findByLogin(principal.getName()));
    }

    /**
     * Загружает галерею пользователя в модель.
     */
    private void loadUserGallery(Model model, Principal principal, int number, int page) {
        model.addAttribute("gallery", imagesService.getGalleryDto(null, principal, number, page));
    }

    /**
     * Загружает галерею пользователя в модель (используется в личной странице).
     */
    private void loadUserGallery(Model model, Principal principal) {
        loadUserGallery(model, principal, PageSize.IMG_9.getPageSize(), PAGE_NUMBER);
    }
}