package com.patientpal.backend.member.repository;

import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import java.util.List;
import java.util.Optional;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("select m.username from Member m where m.username like :username%")
    List<String> findUsernameStartingWith(@Param("username") String username);
}
