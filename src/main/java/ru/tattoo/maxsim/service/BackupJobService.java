package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * «Фасад» запуска бэкапа. Это маленький класс-посредник, который решает одну задачу:
 * атомарно проверить, что бэкап ещё не идёт, и только тогда запустить работу.
 *
 * <p>Почему нельзя просто вызвать {@link BackupWorker} из контроллера? Потому что между
 * «проверить» и «запустить» должна быть одна неделимая операция. Здесь её делает
 * {@link BackupRunRegistry#start()}: он возвращает {@code true} только первому вызову.
 *
 * <p>Заодно это делает контроллер тонким: контроллер не знает, как именно делается бэкап,
 * он лишь спрашивает «получилось запустить?».
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupJobService {

    private final BackupWorker worker;
    private final BackupRunRegistry registry;

    /**
     * Запускает бэкап в фоне.
     *
     * <p>Порядок важен: сначала занимаем слот в реестре, потом отдаём работу воркеру.
     * {@code worker.run()} благодаря {@code @Async} возвращается почти сразу — сам дамп
     * выполняется в другом потоке.
     *
     * @return true, если бэкап запущен; false — если уже выполняется
     */
    public boolean start() {
        if (!registry.start()) {
            return false;
        }
        try {
            worker.run();
        } catch (RuntimeException e) {
            // Например, переполнена очередь executor'а: не оставляем статус «выполняется» навсегда
            registry.failed("Не удалось запустить бэкап: " + e.getMessage());
            throw e;
        }
        return true;
    }

    public BackupRunRegistry.Snapshot status() {
        return registry.current();
    }

    public boolean isRunning() {
        return registry.isRunning();
    }
}
