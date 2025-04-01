package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;

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
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        loadUserGallery(model, principal);       // Загружаем галерею пользователя
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
    public String saveUserReview(@RequestParam("imageName") String imageName, @RequestParam("comment") String comment, Model model, Principal principal) throws IOException, ParseException {
        reviewService.saveImd(imageName, comment, principal.getName()); // Сохраняем отзыв
        loadUserDataIntoModel(model, principal);                         // Обновляем данные пользователя
        return "fragments::input-user-reviews";
    }

    /**
     * Загружает новое тату пользователя.
     */
    @PostMapping("/tattoos-user-import")
    public String uploadUserTattoo(@RequestParam("file") MultipartFile fileImport, Model model, Principal principal) throws IOException, ParseException {
        imagesService.saveImg(fileImport,null,null, principal.getName()); // Сохраняем изображение
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

    /**
     * Загружает галерею татуировок пользователя.
     */
    @GetMapping("/user-tattoos")
    public String loadUserTattoos(Model model, Principal principal) {
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        return "fragments::first-fragment";
    }

    /**
     * Загружает аватар пользователя.
     */
    @PostMapping("/avatar-import")
    public String uploadUserAvatar(@RequestParam("file") MultipartFile fileImport, Model model, Principal principal) throws IOException, ParseException {
        UserDTO user = userService.findByLogin(principal.getName());
        if (user.getAvatar() != null) { // Удаляем старый аватар, если он есть
            userService.deleteImg(user.getId());
        }
        userService.updateUserAvatar(fileImport, user.getId()); // Сохраняем новый аватар
        loadUserDataIntoModel(model, principal);       // Обновляем данные пользователя
        return "lk::avatar";
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