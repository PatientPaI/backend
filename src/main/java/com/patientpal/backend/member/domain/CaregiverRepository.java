package com.patientpal.backend.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {
    Caregiver findByName(String username);

    Caregiver findByMember(Member member);
}
