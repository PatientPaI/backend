package com.patientpal.backend.fixtures.review;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.review.domain.Reviews;
import com.patientpal.backend.review.dto.ReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;

public class ReviewsFixture {
    public static Reviews createReview(Member reviewer, Caregiver reviewed) {
        return Reviews.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .starRating(5)
                .content("Excellent service")
                .build();
    }

    public static ReviewRequest createReviewRequest(Caregiver reviewed) {
        return ReviewRequest.builder()
                .reviewed(reviewed.getName())
                .starRating(5)
                .content("Excellent service")
                .build();
    }

    public static ReviewResponse createReviewResponse() {
        return ReviewResponse.builder()
                .id(1L)
                .reviewerId(1L)
                .reviewerName("John Doe")
                .reviewedId(2L)
                .reviewedName("Caregiver A")
                .starRating(5)
                .content("Excellent service")
                .build();
    }
}

