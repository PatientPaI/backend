package com.patientpal.backend.notification.repository;

import com.patientpal.backend.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverUsernameAndIsReadFalse(String receiverUsername);
}
