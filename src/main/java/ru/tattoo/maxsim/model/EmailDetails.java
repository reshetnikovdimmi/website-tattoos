package ru.tattoo.maxsim.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

    /**
     * Адрес электронной почты получателя.
     * Если null, используется адрес из конфигурации.
     */
    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email должен превышать 100 символов")
    private String recipient;

    /**
     * Имя отправителя.
     */
    @NotBlank(message = "Имя обязательно для заполнения")
    @Size (min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String name;

    /**
     * Текст сообщения.
     */
    @NotBlank(message = "Сообщение не должно быть пустым")
    @Size(min = 10, max = 1000, message = "Сообщение должно содержать от 10 до 1000 символов")
    private String msgBody;


    /**
     * Тема письма или телефон.
     * В текущей реализации используется для номера телефона.
     */
    @NotBlank(message = "Тема/телефон обязателен для заполнения")
    @Size(max = 100, message = "Тема/телефон не должен превышать 100 символов")
    private String subject;

    /**
     * Путь к вложению (если есть).
     */
    @Size(max = 255, message = "Путь к вложению не должен превышать 255 символов")
    private String attachment;
}