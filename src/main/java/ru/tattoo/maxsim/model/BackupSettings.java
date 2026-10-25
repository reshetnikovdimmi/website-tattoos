package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "backup_settings")
@Getter
@Setter
@NoArgsConstructor
public class BackupSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Провайдер хранения. Пока поддерживается только Яндекс.Диск. */
    private String provider = "YANDEX_DISK";

    /**
     * OAuth-токен <b>в зашифрованном виде</b> (AES-GCM, см. {@code CryptoService}).
     * В открытом виде в БД не хранится.
     */
    @Column(length = 1000)
    private String yandexToken;

    /** Папка на Диске, куда складываются копии. */
    private String backupDir = "/tattoo-backups";

    /** Включён ли автоматический бэкап по расписанию. */
    private Boolean enabled = true;

    /** Когда последний раз запускался бэкап (успешный или нет). */
    private LocalDateTime lastBackupAt;

    /** Итог последнего прогона: SUCCESS или FAILED. */
    private String lastBackupStatus;

    /** Когда настройки последний раз менялись. */
    private LocalDateTime updatedAt;
}
