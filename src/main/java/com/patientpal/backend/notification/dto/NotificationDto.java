package com.patientpal.backend.notification.dto;

import com.patientpal.backend.notification.domain.Notification;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    @AllArgsConstructor
    public static class Response {

        String id;

        String name;

        String content;

        String type;

        String createdDate;

        public static Response createResponse(Notification notification) {
            return Response.builder()
                    .content(notification.getContent())
                    .id(notification.getId().toString())
                    .name(notification.getReceiver().getUsername())
                    .type(notification.getNotificationType().toString())
                    .createdDate(notification.getCreatedDate().toString())
                    .build();
        }
    }
}
