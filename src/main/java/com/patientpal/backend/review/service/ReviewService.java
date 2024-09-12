package com.patientpal.backend.review.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.review.domain.Reviews;
import com.patientpal.backend.review.dto.CreateReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.dto.UpdateReviewRequest;
import com.patientpal.backend.review.repository.ReviewRepository;
import com.patientpal.backend.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final CaregiverRepository caregiverRepository;
    private final MatchRepository matchRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest createReviewRequest, String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        Member reviewer = memberRepository.findByUsernameOrThrow(username);
        Member reviewed = memberRepository.findByUsernameOrThrow(createReviewRequest.getReviewedName());

        checkIfReviewAlreadyExists(reviewer, reviewed);
        validateMatch(createReviewRequest.getMatchingId());

        Reviews review = createNewReview(createReviewRequest, reviewer, reviewed);

        Reviews savedReviews = saveReviewAndUpdateRating(review, reviewed);

        return ReviewResponse.fromReview(savedReviews);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long id) {
        Reviews reviews = findReview(id);
        return ReviewResponse.fromReview(reviews);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        Page<Reviews> reviews = reviewRepository.findAll(pageable);
        return reviews.map(ReviewResponse::fromReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsWrittenByUser(String username, Pageable pageable) {
        Member reviewer = memberRepository.findByUsernameOrThrow(username);
        Page<Reviews> reviews = reviewRepository.findByReviewerId(reviewer.getId(), pageable);

        return reviews.map(ReviewResponse::fromReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsReceivedByUser(String username, Pageable pageable) {
        Member reviewed = memberRepository.findByUsernameOrThrow(username);
        Page<Reviews> reviews = reviewRepository.findByReviewedId(reviewed.getId(), pageable);
        return reviews.map(ReviewResponse::fromReview);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, UpdateReviewRequest updateReviewRequest, String username) {

        Reviews reviews = findReview(id);
        Member reviewer = memberRepository.findByUsernameOrThrow(username);

        if (!reviews.getReviewer().equals(reviewer)) {
            throw new EntityNotFoundException(ErrorCode.AUTHORIZATION_FAILED,
                    "You are not authorized to update this review");
        }
        reviews.updateReview(updateReviewRequest);

        return ReviewResponse.fromReview(reviews);
    }

    @Transactional
    public void deleteReview(Long id, String username) {
        Reviews review = findReview(id);

        if (!review.getReviewer().getUsername().equals(username)) {
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
                        .rating(caregiver.getRating())
                        .build())
                .sorted((c1, c2) -> Float.compare(c2.getRating(), c1.getRating()))
                .limit(10)
                .collect(Collectors.toList());
    }


    private Reviews findReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND,
                        "Review not found with id: " + id));
    }

    private Reviews createNewReview(CreateReviewRequest createReviewRequest, Member reviewer, Member reviewed) {
        return Reviews.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .starRating(createReviewRequest.getStarRating())
                .content(createReviewRequest.getContent())
                .build();
    }

    private void validateMatch(Long matchId) {
        Optional<Match> match = matchRepository.findById(matchId);
        if (match.isEmpty() || match.get().getMatchStatus() != MatchStatus.COMPLETED) {
            throw new IllegalArgumentException("리뷰를 작성할 수 없습니다. 매칭이 완료되지 않았습니다.");
        }
    }

    private void checkIfReviewAlreadyExists(Member reviewer, Member reviewed) {
        reviewRepository.findByReviewerIdAndReviewedId(reviewer.getId(), reviewed.getId())
                .ifPresent(existingReview -> {
                    throw new IllegalArgumentException("이미 해당 사용자에 대한 리뷰가 작성되었습니다.");
                });
    }

    private Reviews saveReviewAndUpdateRating(Reviews review, Member reviewed) {
        Reviews savedReviews = reviewRepository.save(review);

        reviewed.addReview(savedReviews);

        float calculatedRating = savedReviews.getCalculatedRating();
        reviewed.addReviewRating(calculatedRating);

        memberRepository.save(reviewed);
        return savedReviews;
    }
}
