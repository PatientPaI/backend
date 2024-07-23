package com.patientpal.backend.review.domain;

import com.patientpal.backend.review.dto.ReviewRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Size(min = 1, max = 5)
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
        switch (this.starRating) {
            case 1:
                return -0.2;
            case 2:
                return -0.1;
            case 3:
                return 0.0;
            case 4:
                return 0.1;
            case 5:
                return 0.2;
            default:
                throw new IllegalArgumentException("Invalid star rating");
        }
    }
}
