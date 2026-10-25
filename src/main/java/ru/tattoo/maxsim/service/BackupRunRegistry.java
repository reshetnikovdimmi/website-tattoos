package ru.tattoo.maxsim.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Память о том, что сейчас происходит с бэкапом: {@code IDLE → RUNNING → SUCCESS/FAILED}.
 *
 * <p><b>Зачем нужен.</b> Бэкап выполняется в фоне, а браузер не может «подождать» его в одном
 * HTTP-запросе. Поэтому фоновая задача пишет сюда статус, а контроллер по запросу
 * {@code GET /admin/backup/status} просто читает его и отдаёт в JSON.
 *
 * <p><b>Почему потокобезопасно.</b> Пишет воркер (поток {@code backup-N}), читает поток Tomcat.
 * Два потока не должны испортить данные, поэтому:
 * <ul>
 *   <li>{@link java.util.concurrent.atomic.AtomicReference} — безопасно заменяет ссылку целиком;</li>
 *   <li>{@code synchronized} на методах — не даёт двум потокам одновременно менять состояние;</li>
 *   <li>{@link Snapshot} — неизменяемая копия состояния (record), её безопасно читать.</li>
 * </ul>
 *
 * <p>Состояние живёт в памяти: после перезапуска приложения оно снова {@code IDLE}.
 * Исторический статус последнего прогона хранится в БД (см. {@link BackupSettingsService}).
 *
 * <p>{@code record} — короткий неизменяемый класс: компилятор сам создаёт конструктор,
 * геттеры и {@code equals/hashCode/toString}.
 */
@Component
public class BackupRunRegistry {

    public enum State {
        IDLE, RUNNING, SUCCESS, FAILED
    }

    public record Snapshot(
        State state,
        String message,
        String dir,
        Instant startedAt,
        Instant finishedAt
    ) {
        public boolean running() {
            return state == State.RUNNING;
        }
    }

    private static final Snapshot IDLE = new Snapshot(State.IDLE, null, null, null, null);

    private final AtomicReference<Snapshot> snapshot = new AtomicReference<>(IDLE);

    /**
     * Переводит состояние в RUNNING. Возвращает false, если бэкап уже идёт.
     */
    public synchronized boolean start() {
        Snapshot current = snapshot.get();
        if (current.running()) {
            return false;
        }
        snapshot.set(new Snapshot(State.RUNNING, "Бэкап выполняется", null, Instant.now(), null));
        return true;
    }

    public synchronized void success(String dir) {
        Snapshot current = snapshot.get();
        snapshot.set(new Snapshot(State.SUCCESS, "Бэкап создан", dir, current.startedAt(), Instant.now()));
    }

    public synchronized void failed(String message) {
        Snapshot current = snapshot.get();
        snapshot.set(new Snapshot(State.FAILED, message, null, current.startedAt(), Instant.now()));
    }

    public Snapshot current() {
        return snapshot.get();
    }

    public boolean isRunning() {
        return snapshot.get().running();
    }
}
