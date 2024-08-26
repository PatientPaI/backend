package com.patientpal.backend.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(min = 1, max = 5)
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
