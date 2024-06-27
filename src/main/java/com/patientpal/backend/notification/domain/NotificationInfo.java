package com.patientpal.backend.notification.domain;

public interface NotificationInfo {

    String getReceiver();

    Long getGoUrlId();

    NotificationType getNotificationType();

}
