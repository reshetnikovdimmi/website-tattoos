package ru.tattoo.maxsim.exceptions;


import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = Map.of();
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public static ValidationException emptyField(String fieldName) {
        return new ValidationException("Поле '" + fieldName + "' не может быть пустым");
    }
}
