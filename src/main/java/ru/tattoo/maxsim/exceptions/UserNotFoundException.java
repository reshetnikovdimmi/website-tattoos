package ru.tattoo.maxsim.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
