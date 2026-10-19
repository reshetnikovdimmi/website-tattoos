package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tattoo.maxsim.model.BackupSettings;

import java.util.Optional;

public interface BackupSettingsRepository extends JpaRepository<BackupSettings, Long> {
    Optional<BackupSettings> findByProvider(String provider);
}
