package ru.tattoo.maxsim.exceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.tattoo.maxsim.exceptions.mail.MailSettingsSaveException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ==================== ПОЛНЫЕ СТРАНИЦЫ ====================

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception ex, Model model) {
        log.error("Unhandled exception: ", ex);
        model.addAttribute("errorMessage", "Произошла ошибка. Пожалуйста, попробуйте позже.");
        model.addAttribute("errorDetails", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-404";
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        log.warn("User not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-404";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        log.warn("Type mismatch: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Неверный формат параметра: " + ex.getName());
        return "error-400";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.warn("Illegal argument: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-400";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMaxUploadSize(MaxUploadSizeExceededException ex, Model model) {
        log.warn("File too large: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Файл слишком большой. Максимальный размер 5MB.");
        return "error-400";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResource(NoResourceFoundException ex, Model model) {
        log.warn("Resource not found: {}", ex.getResourcePath());
        model.addAttribute("errorMessage", "Запрашиваемый ресурс не найден");
        return "error-404";
    }

    @ExceptionHandler(FileDeletionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleFileDeletionError(FileDeletionException ex, Model model) {
        log.error("File deletion error: {}", ex.getMessage());
        model.addAttribute("error", "Ошибка при удалении файла");
        model.addAttribute("errorDetails", ex.getMessage());
        return "modal::modal-body";
    }

    // ==================== ДЛЯ FRAGMENT/AJAX ЗАПРОСОВ ====================

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFileUploadError(FileUploadException ex, Model model) {
        log.warn("File upload error: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("uploadError", ex.getMessage());
        // Возвращаем имя фрагмента с ошибкой вместо null
        return "modal::modal-body";
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationError(ValidationException ex, Model model) {
        log.warn("Validation error: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("validationErrors", ex.getErrors());
        return "modal::modal-body";
    }
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException ex, Model model) {
        log.warn("Entity not found: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "modal::modal-body";  // или "error-404"
    }

    @ExceptionHandler(MailSettingsSaveException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleMailSettingsSaveException(MailSettingsSaveException ex, Model model) {
        log.error("Ошибка сохранения настроек почты: {}", ex.getMessage(), ex);
        model.addAttribute("error", ex.getMessage());
        return "modal::modal-body";  // или "error-404"
    }
}