package ru.tattoo.maxsim.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.tattoo.maxsim.service.BackupJobService;
import ru.tattoo.maxsim.service.BackupRunRegistry;
import ru.tattoo.maxsim.service.BackupSettingsService;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Логика эндпоинтов бэкапа. Доступ по роли ADMIN обеспечивается правилом
 * {@code /admin/**} в SecurityConfig, здесь проверяется контракт ответов.
 */
class BackupEndpointsTest {

    private final BackupSettingsService settingsService = mock(BackupSettingsService.class);
    private final BackupJobService jobService = mock(BackupJobService.class);
    private final BackupSettingsController controller =
        new BackupSettingsController(settingsService, jobService);

    @Test
    void createShouldStartBackup() {
        when(jobService.start()).thenReturn(true);

        Map<String, Object> body = controller.create().getBody();

        assertThat(body).containsEntry("success", true).containsEntry("started", true);
    }

    @Test
    void createShouldRejectSecondRun() {
        when(jobService.start()).thenReturn(false);

        Map<String, Object> body = controller.create().getBody();

        assertThat(body).containsEntry("success", false);
        assertThat((String) body.get("error")).contains("уже выполняется");
    }

    @Test
    void statusShouldExposeRegistryState() {
        Instant started = Instant.parse("2026-01-01T03:00:00Z");
        Instant finished = Instant.parse("2026-01-01T03:00:10Z");
        when(jobService.status()).thenReturn(new BackupRunRegistry.Snapshot(
            BackupRunRegistry.State.SUCCESS, "Бэкап создан", "/tattoo-backups", started, finished));

        ResponseEntity<Map<String, Object>> response = controller.status();

        assertThat(response.getBody())
            .containsEntry("state", "SUCCESS")
            .containsEntry("running", false)
            .containsEntry("dir", "/tattoo-backups")
            .containsEntry("startedAt", started.toString())
            .containsEntry("finishedAt", finished.toString());
    }

    @Test
    void statusShouldReportRunningFlag() {
        when(jobService.status()).thenReturn(new BackupRunRegistry.Snapshot(
            BackupRunRegistry.State.RUNNING, "Бэкап выполняется", null, Instant.now(), null));

        assertThat(controller.status().getBody()).containsEntry("running", true);
    }
}
