package ru.tattoo.maxsim.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceLoggerAspect {

    @Around("execution(* com.yourpackage.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - startTime;

        log.debug("Метод {} выполнен за {} мс",
                joinPoint.getSignature().toShortString(),
                elapsedTime);

        if (elapsedTime > 1000) { // Если выполнение дольше 1 секунды
            log.warn("Метод {} выполняется долго: {} мс",
                    joinPoint.getSignature().toShortString(),
                    elapsedTime);
        }

        return result;
    }
}