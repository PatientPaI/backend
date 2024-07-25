package com.patientpal.backend.review.domain;

import com.patientpal.backend.review.dto.ReviewRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String reviewerName;

    @NotBlank
    private String reviewedName;

    @Min(1)
    @Max(5)
    private int starRating;

    @NotBlank
    private String content;

    @Builder
    public Review(String reviewerName, String reviewedName, int starRating, String content) {
        this.reviewerName = reviewerName;
        this.reviewedName = reviewedName;
        this.starRating = starRating;
        this.content = content;
    }

    public void updateReview(ReviewRequest review) {
        this.reviewerName = review.getReviewerName();
        this.reviewedName = review.getReviewedName();
        this.starRating = review.getStarRating();
        this.content = review.getContent();
    }


    public double getCalculatedRating() {
        return switch (this.starRating) {
            case 1 -> -0.2;
            case 2 -> -0.1;
            case 3 -> 0.0;
            case 4 -> 0.1;
            case 5 -> 0.2;
            default -> throw new IllegalArgumentException("Invalid star rating");
        };
    }
}