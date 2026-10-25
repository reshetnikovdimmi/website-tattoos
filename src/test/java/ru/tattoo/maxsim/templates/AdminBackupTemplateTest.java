package ru.tattoo.maxsim.templates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tattoo.maxsim.config.StorageProperties;
import ru.tattoo.maxsim.controller.admin.BackupSettingsController;
import ru.tattoo.maxsim.model.BackupSettings;
import ru.tattoo.maxsim.model.DTO.DiskItem;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.BackupJobService;
import ru.tattoo.maxsim.service.BackupSettingsService;
import ru.tattoo.maxsim.service.RestoreJobService;
import ru.tattoo.maxsim.service.interf.SettingWebsiteService;
import ru.tattoo.maxsim.service.interf.SiteVerificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Регрессия на ошибку Thymeleaf: в 3.1 нельзя подставлять строки в th:onclick,
 * поэтому имя файла бэкапа передаётся через data-file. Тест рендерит фрагмент
 * через настоящий MVC-конвейер и падает, если шаблон снова сломается.
 */
@WebMvcTest(controllers = BackupSettingsController.class)
@EnableConfigurationProperties(StorageProperties.class)
@ActiveProfiles("test")
class AdminBackupTemplateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackupSettingsService settingsService;

    @MockitoBean
    private BackupJobService jobService;

    @MockitoBean
    private RestoreJobService restoreJobService;

    @MockitoBean
    private ContactInfoRepository contactInfoRepository;

    @MockitoBean
    private SettingWebsiteService settingWebsiteService;

    @MockitoBean
    private SiteVerificationService siteVerificationService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void backupFragmentShouldRender() throws Exception {
        BackupSettings settings = new BackupSettings();
        settings.setBackupDir("/tattoo-backups");
        settings.setEnabled(true);
        settings.setLastBackupAt(LocalDateTime.of(2026, 1, 1, 3, 0, 5));
        settings.setLastBackupStatus("SUCCESS");

        DiskItem db = new DiskItem();
        db.setName("db_2026-01-01_03-00-00.sql.gz");
        db.setType("file");
        db.setSize(1024L * 1024);

        DiskItem images = new DiskItem();
        images.setName("images_2026-01-01_03-00-00.tar.gz");
        images.setType("file");
        images.setSize(2L * 1024 * 1024);

        when(settingsService.getOrCreate()).thenReturn(settings);
        when(settingsService.listBackups()).thenReturn(List.of(db, images));
        when(siteVerificationService.getActiveMetaTag(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/backup/fragment"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("class=\"backup-settings\"")))
            .andExpect(content().string(containsString("Резервные копии")))
            .andExpect(content().string(containsString("01.01.2026 03:00")))
            .andExpect(content().string(containsString("Успешно")))
            .andExpect(content().string(containsString("Восстановить")))
            .andExpect(content().string(containsString("База данных")))
            .andExpect(content().string(containsString("Картинки")))
            .andExpect(content().string(
                containsString("data-file=\"db_2026-01-01_03-00-00.sql.gz\"")))
            .andExpect(content().string(
                containsString("data-file=\"images_2026-01-01_03-00-00.tar.gz\"")))
            .andExpect(content().string(containsString("downloadBackup(this.dataset.file)")))
            .andExpect(content().string(containsString("deleteBackup(this.dataset.file)")));
    }
}
