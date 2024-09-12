package com.patientpal.backend.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.patientpal.backend.review.domain.Reviews;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    Optional<Reviews> findByReviewerIdAndReviewedId(Long reviewerId, Long reviewedId);
    Page<Reviews> findAll(Pageable pageable);
    Page<Reviews> findByReviewerId(Long reviewerId, Pageable pageable);
    Page<Reviews> findByReviewedId(Long reviewedId, Pageable pageable);
}
