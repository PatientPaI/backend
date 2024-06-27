package com.patientpal.backend.notification.aspect;

import com.patientpal.backend.notification.domain.NotificationInfo;
import com.patientpal.backend.notification.domain.NotificationMessage;
import com.patientpal.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationService notificationService;

    @Pointcut("@annotation(com.patientpal.backend.notification.annotation.NeedNotification)")
    public void annotationPointcut() {
    }

    @Async
    @AfterReturning(pointcut = "annotationPointcut()", returning = "result")
    public void checkValue(JoinPoint joinPoint, Object result) throws Throwable {
        NotificationInfo notifyProxy = (NotificationInfo) result;
        notificationService.send(
                notifyProxy.getReceiver(),
                notifyProxy.getNotificationType(),
                NotificationMessage.MATCH_NEW_REQUEST.getMessage(),
                "/api/v1/match/" + (notifyProxy.getGoUrlId())
        );
        log.info("result = {}", result);
    }
}
