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
public class CreateReviewRequest {

    @NotNull
    private Long matchingId;

    @NotNull
    private String reviewedName;

    @Min(1)
    @Max(5)
    private int starRating;

    @NotBlank
    private String content;

    @Builder
    public CreateReviewRequest(Long matchingId, String reviewedName, int starRating, String content) {
        this.matchingId = matchingId;
        this.reviewedName = reviewedName;
        this.starRating = starRating;
        this.content = content;
    }
}
