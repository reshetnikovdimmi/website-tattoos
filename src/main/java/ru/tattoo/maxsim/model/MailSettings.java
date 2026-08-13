package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mail_settings")
public class MailSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== SMTP НАСТРОЙКИ =====

    @NotBlank(message = "SMTP-хост обязателен")
    @Size(max = 255, message = "Хост не должен превышать 255 символов")
    @Column(name = "host", nullable = false, length = 255)
    private String host = "smtp.yandex.ru";

    @NotNull(message = "Порт обязателен")
    @Min(value = 1, message = "Порт должен быть от 1")
    @Max(value = 65535, message = "Порт должен быть до 65535")
    @Column(name = "port", nullable = false)
    private Integer port = 465;

    @NotBlank(message = "Имя пользователя обязательно")
    @Email(message = "Имя пользователя должно быть в формате email")
    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // ===== НАСТРОЙКИ ОТПРАВИТЕЛЯ =====

    @NotBlank(message = "Email отправителя обязателен")
    @Email(message = "Некорректный email отправителя")
    @Size(max = 255, message = "Не более 255 символов")
    @Column(name = "from_email", nullable = false, length = 255)
    private String fromEmail;

    @NotBlank(message = "Имя отправителя обязательно")
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    @Column(name = "from_name", length = 255)
    private String fromName = "Тату-студия Maxsim";

    @NotBlank(message = "Email получателя обязателен")
    @Email(message = "Некорректный email получателя")
    @Size(max = 255, message = "Не более 255 символов")
    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    // ===== БЕЗОПАСНОСТЬ =====

    @NotBlank(message = "Тип шифрования обязателен")
    @Pattern(regexp = "SSL|TLS", message = "Шифрование должно быть SSL или TLS")
    @Column(name = "encryption", nullable = false, length = 10)
    private String encryption = "SSL"; // SSL или TLS

    @Column(name = "auth", nullable = false)
    private Boolean auth = true;

    @Column(name = "debug", nullable = false)
    private Boolean debug = false;

    // ===== ШАБЛОНЫ =====

    @Column(name = "subject_template", length = 500)
    private String subjectTemplate = "Новое сообщение с сайта";

    @Column(name = "auto_reply", nullable = false)
    private Boolean autoReply = false;

    // ===== СЛУЖЕБНЫЕ ПОЛЯ =====

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date updatedAt;

    // ===== ПРЕДВАРИТЕЛЬНАЯ ЗАПИСЬ =====
    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
        updatedAt = new java.util.Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new java.util.Date();
    }
}