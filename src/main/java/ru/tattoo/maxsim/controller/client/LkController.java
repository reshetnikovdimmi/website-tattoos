package ru.tattoo.maxsim.controller.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.Date;

@Slf4j
@Controller
@Validated
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
     * Загружает фрагмент с информацией о пользователе.
     */
    @GetMapping("/user-info")
    public String loadUserInfoFragment(Model model, Principal principal) {
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        return "fragment-lk::review-fragment";
    }

    /**
     * Сохраняет отзыв пользователя.
     */
    @PostMapping("/lk/reviews/import")
    public String saveUserReview(@RequestParam("file") MultipartFile fileImport,
                                 @ModelAttribute("reviewsEntity") ReviewsUser object,
                                 @RequestParam(value = "fragment", required = false) String fragmentName,
                                 Model model, Principal principal) throws IOException, ParseException {

        if (!fileImport.isEmpty()) {
            log.info("Получено file {}", fileImport.getOriginalFilename());
            Images images = new Images();
            images.setDescription(object.getComment());
            Images savedImage = imagesService.saveImg(fileImport, images);

            String savedImageName = savedImage.getImageName();
            log.info("savedImageName {}", savedImageName);
            object.setImageName(savedImageName);
        }
        object.setUserName(principal.getName());
        object.setDate(new Date());
        reviewService.create(object); // Сохраняем отзыв
        loadUserDataIntoModel(model, principal);                         // Обновляем данные пользователя
        return "fragment-lk::"+fragmentName;
    }

    /**
     * Загружает новое тату пользователя.
     */
    @PostMapping("/lk/import/tattoos")
    public String uploadUserTattoo(@RequestParam("file") MultipartFile fileImport,
                                   @ModelAttribute("userTattoo") Images object,
                                   @RequestParam(value = "fragment", required = false) String fragmentName,
                                   Model model,
                                   Principal principal) throws IOException, ParseException {
        object.setUserName(principal.getName());
        imagesService.saveImg(fileImport, object); // Сохраняем изображение
        loadUserDataIntoModel(model, principal);                     // Обновляем данные пользователя
        model.addAttribute("userTattoo", new Images());
        return "fragment-lk::"+fragmentName;
    }

    /**
     * Загружает форму редактирования профиля.
     */
    @GetMapping("/profile-editing")
    public String loadProfileEditForm(Model model, Principal principal) {
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        return "fragment-lk::profile-editing";
    }

    @PostMapping("/lk/update/profile")
    public String updateProfile(@Valid @ModelAttribute("UserDTO") UserDTO userDTO,
                                BindingResult bindingResult,
                                Model model,
                                Principal principal,
                                @RequestParam(value = "fragment", required = false) String fragmentName,
                                @Valid @RequestParam(value = "password", required = false) String password,
                                @Valid @RequestParam(value = "confirm-password", required = false) String confirmPassword) {

        log.info("Обновление профиля для пользователя: {}", principal.getName());
        log.debug("UserDTO: login={}, email={}", userDTO.getLogin(), userDTO.getEmail());

        // Проверяем валидацию
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации: {}", bindingResult.getAllErrors());

            // Собираем все ошибки в одно сообщение
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                if (errorMessage.length() > 0) errorMessage.append("; ");
                errorMessage.append(error.getDefaultMessage());
            });

            loadUserDataIntoModel(model, principal);
            model.addAttribute("userTattoo", new Images());
            model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), 0));
            model.addAttribute("error", errorMessage.toString());
            model.addAttribute("message", null);

            return "lk::profile-editing";
        }

        // Проверка пароля
        if (password != null && !password.trim().isEmpty()) {
            if (password.length() < 6) {
                loadUserDataIntoModel(model, principal);
                model.addAttribute("userTattoo", new Images());
                model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), 0));
                model.addAttribute("error", "Пароль должен содержать минимум 6 символов");
                model.addAttribute("message", null);
                return "lk::profile-editing";
            }

            if (!password.equals(confirmPassword)) {
                loadUserDataIntoModel(model, principal);
                model.addAttribute("userTattoo", new Images());
                model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), 0));
                model.addAttribute("error", "Пароли не совпадают");
                model.addAttribute("message", null);
                return "lk::profile-editing";
            }
        }

        try {
            // Обновляем данные пользователя
            userService.updateUserProfile(userDTO, principal.getName(), password, confirmPassword);

            // Загружаем обновленные данные в модель
            loadUserDataIntoModel(model, principal);
            model.addAttribute("userTattoo", new Images());
            model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), 0));

            return "lk::" + fragmentName;

        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации при обновлении профиля: {}", e.getMessage());

            model.addAttribute("error", e.getMessage());

            return "modal::modal-body";

        } catch (Exception e) {
            log.error("Ошибка при обновлении профиля: {}", e.getMessage(), e);

            model.addAttribute("error", e.getMessage()+": "+e.getCause().getMessage() ==null?""+e.getClass():""+ e);

            return "modal::modal-body";
        }
    }


    /**
     * Загружает галерею татуировок пользователя.
     */
    @GetMapping("/user-tattoos")
    public String loadUserTattoos(Model model, Principal principal) {
        model.addAttribute("userTattoo", new Images());
        loadUserDataIntoModel(model, principal); // Извлекаем данные пользователя
        return "fragment-lk::first-fragment";
    }

    /**
     * Загружает аватар пользователя.
     */
    @PostMapping("/lk/update/avatar")
    public String uploadUserAvatar(@RequestParam("file") MultipartFile fileImport,
                                   Model model,
                                   @RequestParam(value = "fragment", required = false) String fragmentName,
                                   Principal principal) throws IOException, ParseException {

        // Проверка размера файла (максимум 10MB)
        long maxSize = 10 * 1024 * 1024; // 10 MB
        if (fileImport.getSize() > maxSize) {
            model.addAttribute("error", "Размер файла не должен превышать 10MB");
            return "modal::modal-body";
        }

        userService.updateUserAvatar(fileImport, principal); // Сохраняем новый аватар
        loadUserDataIntoModel(model, principal);       // Обновляем данные пользователя
        model.addAttribute("userTattoo", new Images());
        return "lk::"+fragmentName;
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