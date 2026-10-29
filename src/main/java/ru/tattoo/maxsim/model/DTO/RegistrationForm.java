package ru.tattoo.maxsim.model.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegistrationForm {

    @NotBlank(message = "Логин не может быть пустым")
    @Size(min = 2, max = 20, message = "Логин должен содержать от 2 до 20 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Логин может содержать только буквы, цифры, точки, дефисы и нижнее подчеркивание")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 100, message = "Пароль должен содержать от 6 до 100 символов")
    private String password;

    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    private String confirmPassword;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введите корректный email адрес")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Неверный формат email")
    private String email;
}
