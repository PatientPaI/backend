package com.patientpal.backend.notification.controller;

import com.patientpal.backend.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletResponse;
import java.net.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notification")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal User currentMember,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                HttpServletResponse response) {
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");
        return notificationService.subscribe(currentMember.getUsername(), lastEventId);
    }

    @PostMapping("/read")
    public void setReadAllNotification(@AuthenticationPrincipal User currentMember) {
        notificationService.setReadAll(currentMember.getUsername());
    }
}
