package ru.tattoo.maxsim.controller.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.tattoo.maxsim.model.DTO.RegistrationForm;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.model.UserRole;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.validator.UserValidator;

import java.util.Date;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserService userService, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    @PostMapping("/process-registration")
    public String processRegistration(@ModelAttribute("registrationForm") RegistrationForm form, Model model, BindingResult bindingResult) {
        if (!validateForm(form, bindingResult)) {
            return "registration";
        }

        registerUser(form);
        return "login";
    }

    private boolean validateForm(RegistrationForm form, BindingResult bindingResult) {
        userValidator.validateRegistration(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return false;
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "400", "Пароли не совпадают");
            return false;
        }

        return true;
    }

    private void registerUser(RegistrationForm form) {
        User user = new User();
        user.setLogin(form.getLogin());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(UserRole.USER.toString());
        user.setDate(new Date());
        userService.create(user);
    }
}
