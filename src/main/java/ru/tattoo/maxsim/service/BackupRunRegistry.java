package ru.tattoo.maxsim.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Состояние текущего/последнего прогона бэкапа.
 * Нужно, чтобы UI мог опрашивать статус, а приложение — не запускать два бэкапа одновременно.
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
