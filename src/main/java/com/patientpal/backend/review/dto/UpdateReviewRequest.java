package com.patientpal.backend.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReviewRequest {

    @NotNull
    private String reviewedName;

    @Size(min = 1, max = 5)
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
