package ru.tattoo.maxsim.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tattoo.maxsim.model.DTO.BackupResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BackupJobServiceTest {

    @Mock
    private BackupWorker worker;

    private final BackupRunRegistry registry = new BackupRunRegistry();

    private BackupJobService jobService() {
        return new BackupJobService(worker, registry);
    }

    @Test
    void startShouldDelegateToWorker() {
        BackupJobService service = jobService();

        assertThat(service.start()).isTrue();
        verify(worker).run();
        assertThat(service.isRunning()).isTrue();
    }

    @Test
    void startShouldRejectSecondRun() {
        BackupJobService service = jobService();
        service.start();

        assertThat(service.start()).isFalse();
    }

    @Test
    void workerShouldMarkSuccess() {
        BackupService backupService = org.mockito.Mockito.mock(BackupService.class);
        when(backupService.createBackup())
            .thenReturn(new BackupResult("/tattoo-backups", "db_x.sql.gz", "images_x.tar.gz", 10, 20));

        BackupWorker worker = new BackupWorker(backupService, registry);
        registry.start();
        worker.run();

        assertThat(registry.current().state()).isEqualTo(BackupRunRegistry.State.SUCCESS);
        assertThat(registry.current().dir()).isEqualTo("/tattoo-backups");
    }

    @Test
    void workerShouldMarkFailureWithMessage() {
        BackupService backupService = org.mockito.Mockito.mock(BackupService.class);
        doThrow(new RuntimeException("mysqldump не найден")).when(backupService).createBackup();

        BackupWorker worker = new BackupWorker(backupService, registry);
        registry.start();
        worker.run();

        assertThat(registry.current().state()).isEqualTo(BackupRunRegistry.State.FAILED);
        assertThat(registry.current().message()).contains("mysqldump");
    }
}
