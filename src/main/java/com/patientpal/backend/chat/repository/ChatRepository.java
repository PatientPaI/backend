package com.patientpal.backend.chat.repository;

import com.patientpal.backend.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
