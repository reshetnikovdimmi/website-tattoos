package ru.tattoo.maxsim.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.BackupSettings;
import ru.tattoo.maxsim.model.DTO.DiskItem;
import ru.tattoo.maxsim.service.BackupJobService;
import ru.tattoo.maxsim.service.BackupRunRegistry;
import ru.tattoo.maxsim.service.BackupSettingsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin/backup")
@RequiredArgsConstructor
public class BackupSettingsController {

    private final BackupSettingsService settingsService;
    private final BackupJobService backupJobService;

    @GetMapping("/fragment")
    public String getFragment(Model model, @RequestParam(required = false) String fragment) {
        BackupSettings settings = settingsService.getOrCreate();
        List<DiskItem> backups = safeListBackups();

        model.addAttribute("backup", settings);
        model.addAttribute("backups", backups);
        return "fragment-admin :: backup-settings";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BackupSettings form,
                       @RequestParam(required = false) String fragment,
                       Model model) {
        settingsService.save(form);

        model.addAttribute("backup", settingsService.getOrCreate());
        model.addAttribute("backups", safeListBackups());
        return "fragment-admin :: backup-settings";
    }

    @PostMapping("/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean ok = settingsService.testConnection();
            if (ok) {
                result.put("success", true);
                result.put("message", "Подключение установлено");
            } else {
                result.put("success", false);
                result.put("error", "Не удалось подключиться. Проверьте токен.");
            }
        } catch (Exception e) {
            log.error("Ошибка проверки подключения", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Запускает бэкап в фоне и сразу отвечает: дамп + архив + загрузка могут идти минутами,
     * а держать HTTP-запрос всё это время нельзя (таймауты прокси).
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create() {
        Map<String, Object> result = new HashMap<>();
        try {
            if (backupJobService.start()) {
                result.put("success", true);
                result.put("started", true);
                result.put("message", "Бэкап запущен");
            } else {
                result.put("success", false);
                result.put("error", "Бэкап уже выполняется — дождитесь завершения");
            }
        } catch (Exception e) {
            log.error("Ошибка запуска бэкапа", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> status() {
        BackupRunRegistry.Snapshot snapshot = backupJobService.status();

        Map<String, Object> result = new HashMap<>();
        result.put("state", snapshot.state().name());
        result.put("running", snapshot.running());
        result.put("message", snapshot.message());
        result.put("dir", snapshot.dir());
        result.put("startedAt", snapshot.startedAt() == null ? null : snapshot.startedAt().toString());
        result.put("finishedAt", snapshot.finishedAt() == null ? null : snapshot.finishedAt().toString());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@RequestParam String fileName) {
        Map<String, Object> result = new HashMap<>();
        try {
            settingsService.deleteBackup(fileName);
            result.put("success", true);
        } catch (Exception e) {
            log.error("Ошибка удаления бэкапа", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> download(@RequestParam String fileName) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = settingsService.getDownloadUrl(fileName);
            result.put("success", true);
            result.put("url", url);
        } catch (Exception e) {
            log.error("Ошибка получения ссылки на скачивание", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    private List<DiskItem> safeListBackups() {
        try {
            return settingsService.listBackups();
        } catch (Exception e) {
            log.warn("Не удалось получить список бэкапов: {}", e.getMessage());
            return List.of();
        }
    }
}
