package com.patientpal.backend.review.repository;

import com.patientpal.backend.review.domain.Reviews;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findByReviewedName(String reviewedName);
}
