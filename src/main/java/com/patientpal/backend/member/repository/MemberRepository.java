package com.patientpal.backend.member.repository;

import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    default Member findByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, username));
    }

    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}
