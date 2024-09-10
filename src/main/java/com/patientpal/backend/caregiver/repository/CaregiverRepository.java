package com.patientpal.backend.caregiver.repository;

import com.patientpal.backend.caregiver.domain.Caregiver;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long>, CaregiverProfileSearchRepositoryCustom {

    Optional<Caregiver> findByUsername(String username);

    @Query("SELECT c FROM Caregivers c WHERE c.address.addr LIKE %:region%")
    List<Caregiver> findByRegion(@Param("region") String region);

    @Query("SELECT c FROM caregivers c WHERE SUBSTRING(c.address.addr, 1, 2) = :city AND c.isProfilePublic ORDER BY c.rating DESC, c.viewCounts DESC")
    List<Caregiver> findTop5ByAddressOrderByRatingDescViewCountsDesc(@Param("city") String city);

    @Query("SELECT c FROM caregivers c WHERE c.isProfilePublic = true ORDER BY c.viewCounts DESC")
    List<Caregiver> findTop5ByViewCountsDesc();

    @Query("SELECT c FROM caregivers c WHERE c.isProfilePublic = true ORDER BY SIZE(c.receivedReviews) DESC")
    List<Caregiver> findTop5ByReviewCountDesc();

}
