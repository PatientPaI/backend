package com.patientpal.backend.fixtures.review;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.review.domain.Reviews;
import com.patientpal.backend.review.dto.CreateReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.dto.UpdateReviewRequest;

public class ReviewsFixture {
    public static Reviews createReview(Member reviewer, Caregiver reviewed) {
        return Reviews.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .starRating(5)
                .content("Excellent service")
                .build();
    }

    public static CreateReviewRequest createCreateReviewRequest(Caregiver reviewed, Long matchingId) {
        return CreateReviewRequest.builder()
                .matchingId(matchingId)
                .reviewedName(reviewed.getName())
                .starRating(5)
                .content("Excellent service")
                .build();
    }

    public static UpdateReviewRequest createUpdateReviewRequest(Caregiver reviewed) {
        return UpdateReviewRequest.builder()
                .reviewedName(reviewed.getName())
                .starRating(5)
                .content("Updated review content")
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

