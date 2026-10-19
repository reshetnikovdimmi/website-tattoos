package ru.tattoo.maxsim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Планировщик и пул для фоновых задач.
 * Для бэкапов — один поток: два дампа одновременно не нужны.
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
