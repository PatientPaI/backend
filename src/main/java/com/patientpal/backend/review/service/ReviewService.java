package com.patientpal.backend.review.service;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.review.domain.Review;
import com.patientpal.backend.review.dto.ReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final CaregiverRepository caregiverRepository;
    private final MatchRepository matchRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ReviewResponse createReview(ReviewRequest reviewRequest, String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        Member reviewer = memberRepository.findByUsernameOrThrow(username);

        validateMath(reviewRequest);
        Review SavedReview = SavedReview(reviewRequest, reviewer);

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

    private Review SavedReview(ReviewRequest reviewRequest, Member reviewer) {
        Member reviewed = memberRepository.findById(reviewRequest.getReviewed().getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, "Reviewed member not found"));

        return Review.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .starRating(reviewRequest.getStarRating())
                .content(reviewRequest.getContent())
                .build();
    }

    private void validateMath(ReviewRequest reviewRequest) {
        Optional<Match> match = matchRepository.findCompleteMatchForMember(
                reviewRequest.getReviewer().getId());
        if (match.isEmpty()) {
            throw new IllegalArgumentException("리뷰를 작성할 수 없습니다. 매칭이 완료되지 않았습니다.");
        }
    }
}
