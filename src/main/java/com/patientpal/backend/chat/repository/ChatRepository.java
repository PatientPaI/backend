package com.patientpal.backend.chat.repository;

import com.patientpal.backend.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(value = "SELECT * FROM chats c WHERE JSON_CONTAINS(c.memberIds, :memberIdJson, '$')", nativeQuery = true)
    List<Chat> findAllByMemberId(@Param("memberIdJson") String memberIdJson);
}
