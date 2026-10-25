package ru.tattoo.maxsim.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Состояние текущего/последнего восстановления из бэкапа.
 */
@Component
public class RestoreRunRegistry {

    public enum State {
        IDLE, RUNNING, SUCCESS, FAILED
    }

    public record Snapshot(
        State state,
        String stamp,
        String message,
        Instant startedAt,
        Instant finishedAt
    ) {
        public boolean running() {
            return state == State.RUNNING;
        }
    }

    private static final Snapshot IDLE = new Snapshot(State.IDLE, null, null, null, null);

    private final AtomicReference<Snapshot> snapshot = new AtomicReference<>(IDLE);

    public synchronized boolean start(String stamp) {
        if (snapshot.get().running()) {
            return false;
        }
        snapshot.set(new Snapshot(State.RUNNING, stamp, "Восстановление выполняется",
            Instant.now(), null));
        return true;
    }

    public synchronized void success() {
        Snapshot current = snapshot.get();
        snapshot.set(new Snapshot(State.SUCCESS, current.stamp(), "Восстановление завершено",
            current.startedAt(), Instant.now()));
    }

    public synchronized void failed(String message) {
        Snapshot current = snapshot.get();
        snapshot.set(new Snapshot(State.FAILED, current.stamp(), message,
            current.startedAt(), Instant.now()));
    }

    public Snapshot current() {
        return snapshot.get();
    }

    public boolean isRunning() {
        return snapshot.get().running();
    }
}
