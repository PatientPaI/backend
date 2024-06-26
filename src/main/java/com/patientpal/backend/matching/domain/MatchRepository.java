package com.patientpal.backend.matching.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.requestMember.id = :memberId")
    Page<Match> findAllRequest(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT m FROM Match m WHERE m.receivedMember.id = :memberId")
    Page<Match> findAllReceived(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE m.requestMember.id = :requestMemberId AND m.receivedMember.id = :receivedMemberId AND m.matchStatus = 'PENDING'")
    boolean existsPendingMatch(@Param("requestMemberId") Long requestMemberId, @Param("receivedMemberId") Long receivedMemberId);
}
