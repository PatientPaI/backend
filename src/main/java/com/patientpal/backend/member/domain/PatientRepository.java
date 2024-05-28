package com.patientpal.backend.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByName(String username);

    Patient findByMember(Member member);
}
