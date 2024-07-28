package com.patientpal.backend.chat.repository;

import com.patientpal.backend.chat.domain.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select c from Chat c where json_contains(c.memberIds, :memberId)")
    List<Chat> findAllByMemberId(@Param("memberId") Long memberId);
}
