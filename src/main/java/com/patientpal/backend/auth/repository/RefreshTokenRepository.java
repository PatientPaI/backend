package com.patientpal.backend.auth.repository;

import com.patientpal.backend.auth.domain.RefreshToken;
import com.patientpal.backend.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberAndToken(Member member, String token);

    Long deleteByMember(Member member);
}
