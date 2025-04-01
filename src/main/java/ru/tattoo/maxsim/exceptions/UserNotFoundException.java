package ru.tattoo.maxsim.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(long userId) {
        this("Пользователь с ID " + userId + " не найден.");
    }
}
