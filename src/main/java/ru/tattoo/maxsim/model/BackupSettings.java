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

    private String provider = "YANDEX_DISK";

    @Column(length = 1000)
    private String yandexToken;  // зашифрован

    private String backupDir = "/tattoo-backups";

    private Boolean enabled = true;

    private LocalDateTime lastBackupAt;
    private String lastBackupStatus;
    private LocalDateTime updatedAt;
}
