package com.patientpal.backend.patient.repository;

import com.patientpal.backend.patient.domain.Patient;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<Patient, Long>, PatientProfileSearchRepositoryCustom {

    Optional<Patient> findByUsername(String username);

    @Query("SELECT p FROM patients p WHERE p.address.addr = :addr AND p.isProfilePublic ORDER BY p.viewCounts DESC")
    List<Patient> findTop5ByAddressOrderByViewCountsDesc(@Param("addr") String addr);
}
