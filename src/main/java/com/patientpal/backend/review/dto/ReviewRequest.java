package com.patientpal.backend.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequest {

    @NotNull
    private String reviewed;

    @Size(min = 1, max = 5)
    private int starRating;

    @NotBlank
    private String content;

    @Builder
    public ReviewRequest(String reviewed, int starRating, String content) {
        this.reviewed = reviewed;
        this.starRating = starRating;
        this.content = content;
    }
}
