package com.patientpal.backend.review.service;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.review.domain.Review;
import com.patientpal.backend.review.dto.ReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.repository.ReviewRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CaregiverRepository caregiverRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest reviewRequest) {

        // todo 계약이 이루어졌는지 확인하는 코드가 필요함

        Review SavedReview = SavedReview(reviewRequest);

        SavedReview = reviewRepository.save(SavedReview);
        return ReviewResponse.fromReview(SavedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long id) {
        Review review = findReview(id);
        return ReviewResponse.fromReview(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest) {
        Review review = findReview(id);
        review.updateReview(reviewRequest);

        return ReviewResponse.fromReview(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND, "Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public  List<CaregiverRankingResponse> getTopCaregiversByRating(String region) {
        List<Caregiver> caregivers = caregiverRepository.findByRegion(region);

        return caregivers.stream()
                .map(caregiver -> {
                    List<Review> reviews = reviewRepository.findByReviewedName(caregiver.getName());
                    double averageRating = calculateAverageRating(reviews);
                    return CaregiverRankingResponse.builder()
                            .id(caregiver.getId())
                            .name(caregiver.getName())
                            .address(caregiver.getAddress().getAddr())
                            .rating(averageRating)
                            .build();
                })
                .sorted((c1, c2) -> Double.compare(c2.getRating(), c1.getRating()))  // 내림차순 정렬
                .limit(10)
                .collect(Collectors.toList());
    }

    private static double calculateAverageRating(List<Review> reviews) {
        double totalRating = reviews.stream()
                .mapToDouble(Review::getCalculatedRating)
                .sum();
        return reviews.isEmpty() ? 0 : totalRating / reviews.size();
    }

    private Review findReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND,
                        "Review not found with id: " + id));
    }

    private Review SavedReview(ReviewRequest reviewRequest) {
        return Review.builder()
                .reviewerName(reviewRequest.getReviewerName())
                .reviewedName(reviewRequest.getReviewedName())
                .starRating(reviewRequest.getStarRating())
                .content(reviewRequest.getContent())
                .build();
    }
}
