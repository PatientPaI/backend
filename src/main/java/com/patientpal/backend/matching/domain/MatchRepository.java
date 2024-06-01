package com.patientpal.backend.matching.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.patient.id = :patientId AND m.matchStatus != 'CANCELLED'")
    Page<Match> findAllByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    @Query("SELECT m FROM Match m WHERE m.caregiver.id = :caregiverId AND m.matchStatus != 'CANCELLED'")
    Page<Match> findAllByCaregiverId(@Param("caregiverId") Long caregiverId, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE m.patient.id = :patientId AND m.caregiver.id = :caregiverId AND m.matchStatus = 'PENDING'")
    boolean existsPendingMatchForPatient(@Param("patientId") Long patientId, @Param("caregiverId") Long caregiverId);

    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE m.caregiver.id = :caregiverId AND m.patient.id = :patientId AND m.matchStatus = 'PENDING'")
    boolean existsPendingMatchForCaregiver(@Param("caregiverId") Long caregiverId, @Param("patientId") Long patientId);
}
