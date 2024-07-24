package com.patientpal.backend.caregiver.repository;

import com.patientpal.backend.caregiver.domain.Caregiver;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long>, CaregiverProfileSearchRepositoryCustom {

    Optional<Caregiver> findByUsername(String username);

    @Query("select c from caregivers c where c.address.addr = :region")
    List<Caregiver> findByRegion(@Param("region") String region);
}
