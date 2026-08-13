package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.MailSettings;

import java.util.Optional;

@Repository
public interface MailSettingsRepository extends JpaRepository<MailSettings, Long> {

    /**
     * Получить первую запись настроек (должна быть одна)
     */
    Optional<MailSettings> findFirstByOrderByIdAsc();

    /**
     * Проверить, существуют ли настройки
     */
    boolean existsBy();

    /**
     * Получить настройки или создать дефолтные
     */
    default MailSettings getDefaultOrCreate() {
        return findFirstByOrderByIdAsc()
                .orElseGet(this::createDefault);
    }

    /**
     * Создать настройки по умолчанию
     */
    default MailSettings createDefault() {
        MailSettings settings = new MailSettings();
        settings.setHost("smtp.yandex.ru");
        settings.setPort(465);
        settings.setUsername("");
        settings.setPassword("");
        settings.setFromEmail("noreply@yourdomain.com");
        settings.setFromName("Тату-студия Maxsim");
        settings.setRecipientEmail("admin@yourdomain.com");
        settings.setEncryption("SSL");
        settings.setAuth(true);
        settings.setDebug(false);
        settings.setSubjectTemplate("Новое сообщение с сайта");
        settings.setAutoReply(false);
        return save(settings);
    }
}