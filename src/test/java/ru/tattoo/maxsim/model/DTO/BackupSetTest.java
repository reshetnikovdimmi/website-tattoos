package ru.tattoo.maxsim.model.DTO;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BackupSetTest {

    @Test
    void shouldGroupDbAndImagesAndSortNewestFirst() {
        List<BackupSet> sets = BackupSet.from(List.of(
            item("db_2026-01-01_03-00-00.sql.gz", 100L),
            item("images_2026-01-01_03-00-00.tar.gz", 200L),
            item("db_2026-01-02_03-00-00.sql.gz", 300L),
            item("images_2026-01-02_03-00-00.tar.gz", 400L)));

        assertThat(sets).hasSize(2);

        BackupSet latest = sets.get(0);
        assertThat(latest.stamp()).isEqualTo("2026-01-02_03-00-00");
        assertThat(latest.dateLabel()).isEqualTo("02.01.2026 03:00");
        assertThat(latest.hasDb()).isTrue();
        assertThat(latest.hasImages()).isTrue();
        assertThat(latest.totalSize()).isEqualTo(700L);

        assertThat(sets.get(1).dateLabel()).isEqualTo("01.01.2026 03:00");
    }

    @Test
    void shouldHandleMissingImagesFile() {
        List<BackupSet> sets = BackupSet.from(List.of(item("db_2026-01-01_03-00-00.sql.gz", 100L)));

        assertThat(sets).hasSize(1);
        assertThat(sets.get(0).hasImages()).isFalse();
        assertThat(sets.get(0).images()).isNull();
        assertThat(sets.get(0).totalSize()).isEqualTo(100L);
    }

    @Test
    void shouldIgnoreUnrelatedFilesAndDirectories() {
        DiskItem dir = new DiskItem();
        dir.setName("old-folder");
        dir.setType("dir");

        List<BackupSet> sets = BackupSet.from(List.of(
            item("readme.txt", 5L),
            dir,
            item("db_2026-01-01_03-00-00.sql.gz", 100L)));

        assertThat(sets).hasSize(1);
        assertThat(sets.get(0).stamp()).isEqualTo("2026-01-01_03-00-00");
    }

    @Test
    void shouldReturnEmptyForNullOrEmptyInput() {
        assertThat(BackupSet.from(null)).isEmpty();
        assertThat(BackupSet.from(List.of())).isEmpty();
    }

    private DiskItem item(String name, Long size) {
        DiskItem item = new DiskItem();
        item.setName(name);
        item.setType("file");
        item.setSize(size);
        return item;
    }
}
