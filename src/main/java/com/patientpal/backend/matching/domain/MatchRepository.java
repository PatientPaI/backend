package com.patientpal.backend.matching.domain;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.requestMember.id = :memberId ORDER BY m.createdDate DESC")
    Page<Match> findAllRequest(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT m FROM Match m WHERE m.receivedMember.id = :memberId ORDER BY m.createdDate DESC")
    Page<Match> findAllReceived(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE m.requestMember.id = :requestMemberId AND m.receivedMember.id = :receivedMemberId AND m.matchStatus = 'PENDING'")
    boolean existsPendingMatch(@Param("requestMemberId") Long requestMemberId,
                               @Param("receivedMemberId") Long receivedMemberId);

    @Query("SELECT m FROM Match m WHERE m.receivedMember.id = :memberId AND m.careEndDateTime < CURRENT_TIMESTAMP AND m.matchStatus = com.patientpal.backend.matching.domain.MatchStatus.COMPLETED")
    Optional<Match> findCompleteMatchForMember(@Param("memberId") Long memberId);


    @Modifying
    @Query("UPDATE Match m SET m.matchStatus = 'COMPLETED' WHERE m.matchStatus = 'ACCEPTED' AND m.careEndDateTime < :currentDate")
    int updateCompletedMatches(@Param("currentDate") LocalDateTime currentDate);
}
