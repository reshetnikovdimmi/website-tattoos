package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tattoo.maxsim.model.BackupSettings;

/**
 * Автоматический бэкап по расписанию.
 *
 * <p>Spring вызывает {@link #scheduledBackup()} сам, по cron-выражению
 * {@code app.backup.cron}. Формат: {@code секунды минуты часы день_месяца месяц день_недели},
 * поэтому {@code 0 0 3 * * *} — «каждый день в 03:00:00». Время берётся из часов JVM
 * (в контейнере обычно UTC).
 *
 * <p>Метод не делает бэкап сам: он только проверяет условия и просит
 * {@link BackupJobService#start()} запустить фоновую работу.
 *
 * <p>Важно: расписание не должно «падать навсегда». Если внутри случится исключение,
 * Spring может перестать вызывать метод у этого бина, поэтому здесь стоит общий try/catch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BackupScheduler {

    private final BackupSettingsService settingsService;
    private final BackupJobService jobService;

    @Scheduled(cron = "${app.backup.cron:0 0 3 * * *}")
    public void scheduledBackup() {
        try {
            BackupSettings settings = settingsService.getOrCreate();

            if (!Boolean.TRUE.equals(settings.getEnabled())) {
                log.debug("Автобэкап отключён в настройках");
                return;
            }
            if (settings.getYandexToken() == null || settings.getYandexToken().isBlank()) {
                log.warn("Автобэкап пропущен: не задан OAuth-токен Яндекс.Диска");
                return;
            }
            if (!jobService.start()) {
                log.warn("Автобэкап пропущен: предыдущий бэкап ещё выполняется");
                return;
            }
            log.info("Запущен автобэкап по расписанию");
        } catch (Exception e) {
            // Планировщик не должен «умирать» из-за одного неудачного запуска
            log.error("Не удалось запустить автобэкап: {}", e.getMessage(), e);
        }
    }
}
