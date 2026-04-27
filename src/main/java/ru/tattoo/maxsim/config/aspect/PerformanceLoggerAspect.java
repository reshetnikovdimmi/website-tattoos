package ru.tattoo.maxsim.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceLoggerAspect {

    // Исправьте путь на ваш реальный пакет
    @Around("execution(* ru.tattoo.maxsim.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - startTime;

        log.debug("Метод {} выполнен за {} мс",
                joinPoint.getSignature().toShortString(),
                elapsedTime);

        if (elapsedTime > 1000) {
            log.warn("Метод {} выполняется долго: {} мс",
                    joinPoint.getSignature().toShortString(),
                    elapsedTime);
        }

        return result;
    }
}