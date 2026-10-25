package ru.tattoo.maxsim.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

/**
 * «Пропуск» только для одной тяжёлой операции одновременно.
 *
 * <p>Дамп БД и восстановление нельзя делать параллельно: восстановление перезаписывает данные,
 * а дамп в этот момент может прочитать «полусостояние». Поэтому и бэкап, и restore
 * сначала пытаются взять этот пропуск, а в {@code finally} возвращают его.
 *
 * <p>{@link Semaphore} с одним разрешением — самый простой вариант. В отличие от
 * {@code ReentrantLock}, семафор не «узнаёт» поток: даже тот же самый поток не сможет
 * взять второй пропуск, пока не вернул первый. Это защищает от случайной вложенности.
 */
@Component
public class BackupOperationLock {

    /** Семафор, а не ReentrantLock: повторный захват в том же потоке тоже должен падать. */
    private final Semaphore permit = new Semaphore(1);

    public boolean tryLock() {
        return permit.tryAcquire();
    }

    public void unlock() {
        permit.release();
    }
}
