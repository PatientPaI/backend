package com.patientpal.backend.patient.repository;

import com.patientpal.backend.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long>, PatientProfileSearchRepositoryCustom {
}
