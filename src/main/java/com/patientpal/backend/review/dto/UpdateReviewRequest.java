package com.patientpal.backend.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReviewRequest {

    @NotNull
    private String reviewedName;

    @Min(1)
    @Max(5)
    private int starRating;

    @NotBlank
    private String content;

    @Builder
    public UpdateReviewRequest(String reviewedName, int starRating, String content) {
        this.reviewedName = reviewedName;
        this.starRating = starRating;
        this.content = content;
    }
}
