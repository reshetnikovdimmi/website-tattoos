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
    @Autowired
    private UserService userService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/process-registration")
    public String processRegistration(@ModelAttribute("user") @Valid User user,Model model,
                                      BindingResult bindingResult) {

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("login", bindingResult);
            return "registration";
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(UserRole.USER.toString());
        user.setDate(new Date());
        userService.create(user);
        return "login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") User user) {
        return "registration";
    }
}