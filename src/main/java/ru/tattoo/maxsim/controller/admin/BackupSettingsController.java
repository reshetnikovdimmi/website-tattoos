package ru.tattoo.maxsim.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.BackupSettings;
import ru.tattoo.maxsim.model.DTO.BackupSet;
import ru.tattoo.maxsim.model.DTO.DiskItem;
import ru.tattoo.maxsim.service.BackupJobService;
import ru.tattoo.maxsim.service.BackupRunRegistry;
import ru.tattoo.maxsim.service.BackupSettingsService;
import ru.tattoo.maxsim.service.RestoreJobService;
import ru.tattoo.maxsim.service.RestoreRunRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP-слой бэкапов: принимает запросы из админки и вызывает сервисы.
 *
 * <p><b>Правило тонкого контроллера.</b> Здесь нет бизнес-логики: контроллер только
 * разбирает параметры, вызывает сервис и возвращает ответ (HTML-фрагмент или JSON).
 * Поэтому его легко читать: по методам видно, какие действия доступны пользователю.
 *
 * <p><b>Почему два вида ответов.</b> {@code getFragment}/{@code save} возвращают имя
 * Thymeleaf-фрагмента — его JavaScript вставляет в страницу. Методы с {@code @ResponseBody}
 * возвращают JSON: их вызывает AJAX из {@code main.js}.
 *
 * <p><b>Почему create/restore отвечают сразу.</b> Дамп и восстановление длятся минутами;
 * держать HTTP-запрос всё это время нельзя (таймауты). Поэтому эндпоинт только <em>запускает</em>
 * фоновую работу, а браузер потом опрашивает {@code /status}.
 */
@Slf4j
@Controller
@RequestMapping("/admin/backup")
@RequiredArgsConstructor
public class BackupSettingsController {

    private final BackupSettingsService settingsService;
    private final BackupJobService backupJobService;
    private final RestoreJobService restoreJobService;

    /** Сколько последних копий хранится на Диске — показываем в подсказке. */
    @Value("${app.backup.keep:3}")
    private int keepBackups = 3;

    /**
     * Отдаёт HTML-фрагмент блока «Бэкап»: настройки + список копий.
     * Вызывается при открытии вкладки и после сохранения/удаления/восстановления.
     */
    @GetMapping("/fragment")
    public String getFragment(Model model, @RequestParam(required = false) String fragment) {
        BackupSettings settings = settingsService.getOrCreate();

        model.addAttribute("backup", settings);
        model.addAttribute("backupSets", toBackupSets(safeListBackups()));
        model.addAttribute("backupKeep", keepBackups);
        return "fragment-admin :: backup-settings";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BackupSettings form,
                       @RequestParam(required = false) String fragment,
                       Model model) {
        settingsService.save(form);

        model.addAttribute("backup", settingsService.getOrCreate());
        model.addAttribute("backupSets", toBackupSets(safeListBackups()));
        model.addAttribute("backupKeep", keepBackups);
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
    /**
     * Запускает бэкап в фоне и сразу отвечает JSON.
     * Статус выполнения потом читается через {@link #status()}.
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create() {
        Map<String, Object> result = new HashMap<>();
        try {
            if (restoreJobService.isRunning()) {
                result.put("success", false);
                result.put("error", "Идёт восстановление из копии — дождитесь завершения");
                return ResponseEntity.ok(result);
            }
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

    /**
     * Текущее состояние фонового бэкапа (его опрашивает JS каждые 3 секунды).
     */
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

    /**
     * Восстановление из копии. Destructive: перезаписывает БД и картинки,
     * поэтому требуется явное подтверждение (confirmation=RESTORE).
     */
    @PostMapping("/restore")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> restore(@RequestParam String stamp,
                                                       @RequestParam(required = false) String confirmation) {
        Map<String, Object> result = new HashMap<>();

        if (!"RESTORE".equals(confirmation)) {
            result.put("success", false);
            result.put("error", "Восстановление не подтверждено");
            return ResponseEntity.ok(result);
        }
        if (backupJobService.isRunning()) {
            result.put("success", false);
            result.put("error", "Идёт создание бэкапа — дождитесь завершения");
            return ResponseEntity.ok(result);
        }

        try {
            if (restoreJobService.start(stamp)) {
                result.put("success", true);
                result.put("started", true);
                result.put("message", "Восстановление запущено");
            } else {
                result.put("success", false);
                result.put("error", "Восстановление уже выполняется");
            }
        } catch (Exception e) {
            log.error("Ошибка запуска восстановления", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/restore/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> restoreStatus() {
        RestoreRunRegistry.Snapshot snapshot = restoreJobService.status();

        Map<String, Object> result = new HashMap<>();
        result.put("state", snapshot.state().name());
        result.put("running", snapshot.running());
        result.put("stamp", snapshot.stamp());
        result.put("message", snapshot.message());
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

    private List<BackupSet> toBackupSets(List<DiskItem> files) {
        return BackupSet.from(files);
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
