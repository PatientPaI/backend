package com.patientpal.backend.review.dto;

import com.patientpal.backend.review.domain.Reviews;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewResponse {

    private Long id;
    private Long reviewerId;
    private String reviewerName;
    private Long reviewedId;
    private String reviewedName;
    private int starRating;
    private String content;

    @Builder
    public ReviewResponse(Long id, Long reviewerId, String reviewerName, Long reviewedId, String reviewedName, int starRating, String content) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewedId = reviewedId;
        this.reviewedName = reviewedName;
        this.starRating = starRating;
        this.content = content;
    }

    public static ReviewResponse fromReview(Reviews reviews) {
        return ReviewResponse.builder()
                .id(reviews.getId())
                .reviewerId(reviews.getReviewer().getId())
                .reviewerName(reviews.getReviewer().getName())
                .reviewedId(reviews.getReviewed().getId())
                .reviewedName(reviews.getReviewed().getName())
                .starRating(reviews.getStarRating())
                .content(reviews.getContent())
                .build();
    }
}
