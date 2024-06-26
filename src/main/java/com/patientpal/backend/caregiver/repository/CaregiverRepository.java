package com.patientpal.backend.caregiver.repository;

import com.patientpal.backend.caregiver.domain.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long>, CaregiverProfileSearchRepositoryCustom {
}
