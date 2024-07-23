package com.patientpal.backend.review.repository;

import com.patientpal.backend.review.domain.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedName(String reviewedName);
}
