package com.patientpal.backend.patient.repository;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByMember(Member member);

    Optional<Patient> findByName(String username);
}
