package com.patientpal.backend.review.fixtures;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.review.domain.Review;
import com.patientpal.backend.review.dto.ReviewRequest;

public class ReviewFixture {
    public static Review createReview() {
        Member reviewer = MemberFixture.createMember(1L, "john_doe", "John Doe");
        Member reviewed = MemberFixture.createMember(2L, "caregiver_a", "Caregiver A");

        return Review.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .starRating(5)
                .content("Excellent service")
                .build();
    }

    public static ReviewRequest createReviewRequest() {
        Member reviewer = MemberFixture.createMember(1L, "john_doe", "John Doe");
        Member reviewed = MemberFixture.createMember(2L, "caregiver_a", "Caregiver A");

        return ReviewRequest.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .starRating(5)
                .content("Excellent service")
                .build();
    }
}

