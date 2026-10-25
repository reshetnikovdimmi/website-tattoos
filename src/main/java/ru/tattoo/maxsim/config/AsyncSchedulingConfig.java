package ru.tattoo.maxsim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Включает два механизма Spring и настраивает пул потоков:
 * <ul>
 *   <li>{@code @EnableScheduling} — разрешает {@code @Scheduled} (автобэкап по cron);</li>
 *   <li>{@code @EnableAsync} — разрешает {@code @Async} (бэкап/восстановление в фоне);</li>
 *   <li>бин {@code backupExecutor} — пул из одного потока с очередью на одну задачу.</li>
 * </ul>
 *
 * <p>Почему один поток: две тяжёлые операции (два дампа или дамп + restore) одновременно
 * не нужны и могут мешать друг другу. Плюс так проще рассуждать о состоянии:
 * в любой момент выполняется максимум одна задача.
 */
@Configuration
@EnableScheduling
@EnableAsync
public class AsyncSchedulingConfig {

    @Bean(name = "backupExecutor")
    public Executor backupExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("backup-");
        executor.initialize();
        return executor;
    }
}
