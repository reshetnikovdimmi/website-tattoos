package ru.tattoo.maxsim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tattoo.maxsim.model.BackupSettings;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BackupSchedulerTest {

    @Mock
    private BackupSettingsService settingsService;

    @Mock
    private BackupJobService jobService;

    private BackupScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new BackupScheduler(settingsService, jobService);
    }

    @Test
    void shouldSkipWhenDisabled() {
        when(settingsService.getOrCreate()).thenReturn(settings(false, "token"));

        scheduler.scheduledBackup();

        verify(jobService, never()).start();
    }

    @Test
    void shouldSkipWhenTokenMissing() {
        when(settingsService.getOrCreate()).thenReturn(settings(true, "  "));

        scheduler.scheduledBackup();

        verify(jobService, never()).start();
    }

    @Test
    void shouldStartWhenEnabledAndTokenPresent() {
        when(settingsService.getOrCreate()).thenReturn(settings(true, "encrypted-token"));
        when(jobService.start()).thenReturn(true);

        scheduler.scheduledBackup();

        verify(jobService).start();
    }

    @Test
    void shouldNotFailWhenPreviousRunIsStillRunning() {
        when(settingsService.getOrCreate()).thenReturn(settings(true, "encrypted-token"));
        when(jobService.start()).thenReturn(false);

        assertThatCode(() -> scheduler.scheduledBackup()).doesNotThrowAnyException();
    }

    @Test
    void shouldNotPropagateSettingsErrors() {
        when(settingsService.getOrCreate()).thenThrow(new RuntimeException("БД недоступна"));

        assertThatCode(() -> scheduler.scheduledBackup()).doesNotThrowAnyException();
        verify(jobService, never()).start();
    }

    private BackupSettings settings(boolean enabled, String token) {
        BackupSettings settings = new BackupSettings();
        settings.setEnabled(enabled);
        settings.setYandexToken(token);
        settings.setBackupDir("/tattoo-backups");
        return settings;
    }
}
