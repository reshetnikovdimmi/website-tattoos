package ru.tattoo.maxsim.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Логин не может быть пустым")
    @Size(min = 2, max = 20, message = "Логин должен содержать от 2 до 20 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Логин может содержать только буквы, цифры, точки, дефисы и нижнее подчеркивание")
    @Column(unique=true)
    private String login;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 100, message = "Пароль должен содержать от 6 до 100 символов")
    private String password;
    @NotBlank(message = "Роль не может быть пустой")
    private String role;
    @Column(unique=true)
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введите корректный email адрес")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Неверный формат email")
    private String email;
    @Pattern(regexp = "^.*\\.(jpg|jpeg|png|gif|webp)$", message = "Допустимые форматы: JPG, JPEG, PNG, GIF, WEBP")
    private String avatar;
    @NotNull(message = "Дата регистрации не может быть пустой")
    @PastOrPresent(message = "Дата регистрации не может быть в будущем")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;
    @Transient
    private Integer pagesPerSheet;

    @OneToMany(targetEntity = ReviewsUser.class, cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "userName", referencedColumnName = "login")

    private List<ReviewsUser> reviews;

    @OneToMany(targetEntity = Images.class, cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "userName", referencedColumnName = "login")

    private List<Images> images;
}
