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
import com.patientpal.backend.review.domain.Reviews;
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
        Caregiver reviewed = caregiverRepository.findByUsername(reviewRequest.getReviewed())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST,
                        reviewRequest.getReviewed()));

        validateMath(reviewer.getId());
        Reviews savedReviews = SavedReview(reviewer, reviewed, reviewRequest);

        reviewed.addReview(savedReviews);

        float calculatedRating = savedReviews.getCalculatedRating();
        reviewed.addReviewRating(calculatedRating);
        caregiverRepository.save(reviewed);

        savedReviews = reviewRepository.save(savedReviews);
        return ReviewResponse.fromReview(savedReviews);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long id) {
        Reviews reviews = findReview(id);
        return ReviewResponse.fromReview(reviews);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest, String username) {

        Reviews reviews = findReview(id);
        Member reviewer = memberRepository.findByUsernameOrThrow(username);

        if (!reviews.getReviewer().equals(reviewer)) {
            throw new EntityNotFoundException(ErrorCode.AUTHORIZATION_FAILED,
                    "You are not authorized to update this review");
        }

        reviews.updateReview(reviewRequest);

        return ReviewResponse.fromReview(reviews);
    }

    @Transactional
    public void deleteReview(Long id, String username) {
        Reviews review = findReview(id);
        Member reviewer = memberRepository.findByUsernameOrThrow(username);

        if (!review.getReviewer().equals(reviewer)) {
            throw new EntityNotFoundException(ErrorCode.AUTHORIZATION_FAILED,
                    "You are not authorized to delete this review");
        }

        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public  List<CaregiverRankingResponse> getTopCaregiversByRating(String region) {
        List<Caregiver> caregivers = caregiverRepository.findByRegion(region);

        return caregivers.stream()
                .map(caregiver -> CaregiverRankingResponse.builder()
                        .id(caregiver.getId())
                        .name(caregiver.getName())
                        .address(caregiver.getAddress().getAddr())
                        .rating(caregiver.getRating())  // 저장된 평점을 바로 사용
                        .build())
                .sorted((c1, c2) -> Float.compare(c2.getRating(), c1.getRating()))  // 내림차순 정렬
                .limit(10)
                .collect(Collectors.toList());
    }

    private Reviews findReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND,
                        "Review not found with id: " + id));
    }

    private Reviews SavedReview(Member reviewer, Caregiver reviewed, ReviewRequest reviewRequest) {

        return Reviews.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .starRating(reviewRequest.getStarRating())
                .content(reviewRequest.getContent())
                .build();
    }

    private void validateMath(Long reviewedId) {
        Optional<Match> match = matchRepository.findCompleteMatchForMember(reviewedId);
        if (match.isEmpty()) {
            throw new IllegalArgumentException("리뷰를 작성할 수 없습니다. 매칭이 완료되지 않았습니다.");
        }
    }
}
