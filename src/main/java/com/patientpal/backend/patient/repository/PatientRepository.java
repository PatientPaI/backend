package com.patientpal.backend.patient.repository;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.patient.domain.Patient;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<Patient, Long>, PatientProfileSearchRepositoryCustom {

    Optional<Patient> findByUsername(String username);

    @Query("SELECT p FROM patients p WHERE SUBSTRING(p.address.addr, 1, 2) = :city AND p.isProfilePublic ORDER BY p.viewCounts DESC")
    List<Patient> findTop5ByAddressOrderByViewCountsDesc(@Param("city") String city);

    @Query("SELECT p FROM patients p WHERE p.isProfilePublic = true ORDER BY p.viewCounts DESC")
    List<Patient> findTop5ByViewCountsDesc();

    @Query("SELECT p FROM patients p WHERE p.isProfilePublic = true ORDER BY SIZE(p.receivedReviews) DESC")
    List<Patient> findTop5ByReviewCountDesc();
}
