package ru.tattoo.maxsim.model.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.ReviewsUser;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserDTO {
    private Long id;
    @NotBlank(message = "Логин не может быть пустым")
    @Size(min = 2, max = 20, message = "Логин должен содержать от 2 до 20 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Логин может содержать только буквы, цифры, точки, дефисы и нижнее подчеркивание")
    private String login;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введите корректный email адрес")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Неверный формат email")
    private String email;
    @Pattern(regexp = "^.*\\.(jpg|jpeg|png|gif|webp)$", message = "Допустимые форматы: JPG, JPEG, PNG, GIF, WEBP")
    private String avatar;
    @PastOrPresent(message = "Дата регистрации не может быть в будущем")
    private Date date;
    private List<ReviewsUser> reviews;
    private List<Images> images;
}
