package ru.tattoo.maxsim.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.model.UserRole;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
public class RegistrationController {

    // Паттерн Service Layer: выделение логики в отдельный сервис
    private final UserService userService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    // Конструктор для внедрения зависимостей
    public RegistrationController(UserService userService, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/process-registration")
    public String processRegistration(@ModelAttribute("user") @Valid User user, Model model, BindingResult bindingResult) {
        if (!validateUser(user, bindingResult)) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "registration";
        }

        registerUser(user);
        return "login";
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    // Метод для валидации пользователя
    private boolean validateUser(User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        return !bindingResult.hasErrors();
    }

    // Метод для регистрации нового пользователя
    private void registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(UserRole.USER.toString());
        user.setDate(new Date());
        userService.create(user);
    }
}