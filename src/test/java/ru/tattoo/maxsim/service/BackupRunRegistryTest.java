package ru.tattoo.maxsim.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BackupRunRegistryTest {

    private final BackupRunRegistry registry = new BackupRunRegistry();

    @Test
    void shouldNotStartSecondRunWhileRunning() {
        assertThat(registry.start()).isTrue();
        assertThat(registry.isRunning()).isTrue();

        assertThat(registry.start()).isFalse();
        assertThat(registry.current().state()).isEqualTo(BackupRunRegistry.State.RUNNING);
    }

    @Test
    void shouldAllowNewRunAfterSuccess() {
        registry.start();
        registry.success("/tattoo-backups");

        assertThat(registry.current().state()).isEqualTo(BackupRunRegistry.State.SUCCESS);
        assertThat(registry.current().dir()).isEqualTo("/tattoo-backups");
        assertThat(registry.current().finishedAt()).isNotNull();
        assertThat(registry.start()).isTrue();
    }

    @Test
    void shouldStoreFailureMessage() {
        registry.start();
        registry.failed("Диск недоступен");

        assertThat(registry.current().state()).isEqualTo(BackupRunRegistry.State.FAILED);
        assertThat(registry.current().message()).isEqualTo("Диск недоступен");
        assertThat(registry.isRunning()).isFalse();
        assertThat(registry.start()).isTrue();
    }
}
