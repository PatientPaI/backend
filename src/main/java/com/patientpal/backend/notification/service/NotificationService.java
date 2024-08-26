package com.patientpal.backend.notification.service;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.notification.domain.Notification;
import com.patientpal.backend.notification.domain.NotificationType;
import com.patientpal.backend.notification.dto.NotificationDto;
import com.patientpal.backend.notification.repository.EmitterRepository;
import com.patientpal.backend.notification.repository.NotificationRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    public SseEmitter subscribe(String username, String lastEventId) {
        String emitterId = makeTimeIncludeId(username);
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        SseEmitter emitter = emitterRepository.save(emitterId, sseEmitter);
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(sseEmitter::complete);

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(username);
        // sendNotification(emitter, eventId, emitterId, "EventStream Created. [username=" + username + "]");
        sendNotification(emitter, eventId, emitterId, "{\"type\": \"CONNECT\", \"message\": \"EventStream Created. [username=" + username + "]\"}");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, username, emitterId, emitter);
        }

        // 로그아웃 동안 받은 알림 전송
        sendUnseenNotifications(username, emitter);

        return emitter;
    }

    private String makeTimeIncludeId(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    private void sendUnseenNotifications(String username, SseEmitter emitter) {
        List<Notification> unseenNotifications = notificationRepository.findByReceiverUsernameAndIsReadFalse(username);

        unseenNotifications.forEach(notification -> {
            String eventId = notification.getReceiver().getUsername() + "_" + System.currentTimeMillis();
            sendNotification(emitter, eventId, eventId, NotificationDto.Response.createResponse(notification));
        });
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("message")
                    .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userEmail));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public void send(String username, NotificationType notificationType, String content, String url) {
        Notification notification = notificationRepository.save(createNotification(username, notificationType, content, url));

        String eventId = username + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(username);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationDto.Response.createResponse(notification));
                }
        );
    }

    private Notification createNotification(String username, NotificationType notificationType, String content, String url) {
        return Notification.builder()
                .receiver(getMember(username))
                .notificationType(notificationType)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }

    public Member getMember(String username) {
        return memberService.getUserByUsername(username);
    }

    @Transactional
    public void setReadAll(String username) {
        List<Notification> unseenNotifications = notificationRepository.findByReceiverUsernameAndIsReadFalse(username);

        unseenNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
}
