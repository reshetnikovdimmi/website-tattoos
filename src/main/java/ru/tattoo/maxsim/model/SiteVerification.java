package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "site_verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SearchEngine engine; // YANDEX, GOOGLE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationMethod method; // HTML_FILE, META_TAG, DNS

    @Column(name = "verification_value", length = 500)
    private String value; // содержимое мета-тега, имя файла или TXT-запись

    @Column(name = "file_content", length = 2000)
    private String fileContent; // для HTML-файла — его содержимое

    @Column(name = "is_active")
    private Boolean active = true;

    @Column(name = "verified")
    private Boolean verified = false; // подтверждено ли (ручной флаг админа)

    public enum SearchEngine {
        YANDEX("Яндекс.Вебмастер"),
        GOOGLE("Google Search Console");

        private final String displayName;

        SearchEngine(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum VerificationMethod {
        HTML_FILE("HTML-файл", "Загрузите файл в корень сайта"),
        META_TAG("Мета-тег", "Вставьте тег в <head> главной страницы"),
        DNS("DNS-запись", "Добавьте TXT-запись у регистратора");

        private final String displayName;
        private final String hint;

        VerificationMethod(String displayName, String hint) {
            this.displayName = displayName;
            this.hint = hint;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getHint() {
            return hint;
        }
    }
}