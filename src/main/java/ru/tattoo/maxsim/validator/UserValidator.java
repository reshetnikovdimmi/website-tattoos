package ru.tattoo.maxsim.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.service.impl.UserDetailsServiceImpl;

import java.util.Optional;

@Component
public class UserValidator implements Validator {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        User userToValidate = (User) obj;
        Optional<User> userFromDB = userDetailsService.loadUserOptionalByUsername(userToValidate.getLogin());
        if (userFromDB.isPresent()) {
            errors.rejectValue("login", "400", "Username already exists");
        }

    }
}
