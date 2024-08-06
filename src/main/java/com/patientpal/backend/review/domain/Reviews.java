package com.patientpal.backend.review.domain;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.review.dto.ReviewRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Member reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_id", nullable = false)
    private Member reviewed;

    @Min(1)
    @Max(5)
    private int starRating;

    @NotBlank
    private String content;

    @Builder
    public Reviews(Member reviewer, Member reviewed, int starRating, String content) {
        this.reviewer = reviewer;
        this.reviewed = reviewed;
        this.starRating = starRating;
        this.content = content;
    }

    public void updateReview(ReviewRequest review) {
        this.reviewer = review.getReviewer();
        this.reviewed = review.getReviewed();
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
