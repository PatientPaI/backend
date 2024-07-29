package com.patientpal.backend.review.dto;

import com.patientpal.backend.review.domain.Review;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewResponse {

    private Long id;
    private String reviewerName;
    private String reviewedName;
    private int starRating;
    private String content;

    @Builder
    public ReviewResponse(Long id, String reviewerName, String reviewedName, int starRating, String content) {
        this.id = id;
        this.reviewerName = reviewerName;
        this.reviewedName = reviewedName;
        this.starRating = starRating;
        this.content = content;
    }

    public static ReviewResponse fromReview(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerName(review.getReviewerName())
                .reviewedName(review.getReviewedName())
                .starRating(review.getStarRating())
                .content(review.getContent())
                .build();
    }
}
