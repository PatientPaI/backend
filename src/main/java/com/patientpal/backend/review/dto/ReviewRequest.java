package com.patientpal.backend.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequest {

    @NotBlank
    private String reviewerName;

    @NotBlank
    private String reviewedName;

    @Size(min = 1, max = 5)
    private int starRating;

    @NotBlank
    private String content;

    @Builder
    public ReviewRequest(String reviewerName, String reviewedName, int starRating, String content) {
        this.reviewerName = reviewerName;
        this.reviewedName = reviewedName;
        this.starRating = starRating;
        this.content = content;
    }
}
