package ru.clevertec.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.clevertec.util.Constants;

import java.lang.reflect.Method;

import static java.util.Optional.ofNullable;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void anyServices() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void anyControllers() {
    }

    @Before("anyControllers()")
    public void beforeControllerMethods(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        log.debug(Constants.LOG_MESSAGE_BEFORE,
                method.getName(),
                args);
    }

    @AfterReturning(pointcut = "anyControllers()", returning = "result")
    public void afterControllerMethods(JoinPoint joinPoint, Object result) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        log.debug(Constants.LOG_MESSAGE_AFTER_RETURNING,
                method.getName(),
                ofNullable(result)
                        .orElse(Constants.EMPTY_STRING));
    }

    @AfterThrowing(pointcut = "anyServices()", throwing = "exception")
    private void afterThrowingAnyServicesMethodsLoggingAdvice(JoinPoint joinPoint, Throwable exception) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        log.debug(Constants.LOG_MESSAGE_AFTER_THROWING,
                method.getName(),
                exception.getMessage());
    }
}
