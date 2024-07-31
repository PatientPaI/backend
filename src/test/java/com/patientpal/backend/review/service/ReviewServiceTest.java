package com.patientpal.backend.review.service;

import static com.patientpal.backend.review.fixtures.CaregiverFixture.createCaregiver;
import static com.patientpal.backend.review.fixtures.MatchFixture.createMatch;
import static com.patientpal.backend.review.fixtures.MemberFixture.createMember;
import static com.patientpal.backend.review.fixtures.ReviewFixture.createReview;
import static com.patientpal.backend.review.fixtures.ReviewFixture.createReviewRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.review.domain.Review;
import com.patientpal.backend.review.dto.ReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.repository.ReviewRepository;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Caregiver caregiver;
    private Review review;
    private ReviewRequest reviewRequest;

    private Match match;

    @BeforeEach
    void setUp() {
        caregiver = createCaregiver();
        review = createReview();
        reviewRequest = createReviewRequest();
        match = createMatch();
    }

    @Nested
    class 리뷰_생성 {

        @Test
        void 성공적으로_리뷰를_생성한다() {
            when(matchRepository.findCompleteMatchForCaregiver(anyLong())).thenReturn(Optional.of(match));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(memberRepository.findById(reviewRequest.getReviewer().getId())).thenReturn(Optional.of(reviewRequest.getReviewer()));
            when(memberRepository.findById(reviewRequest.getReviewed().getId())).thenReturn(Optional.of(reviewRequest.getReviewed()));

            ReviewResponse reviewResponse = reviewService.createReview(reviewRequest);

            assertThat(reviewResponse.getReviewerName()).isEqualTo(review.getReviewer().getName());
            assertThat(reviewResponse.getReviewedName()).isEqualTo(review.getReviewed().getName());
            assertThat(reviewResponse.getStarRating()).isEqualTo(review.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(review.getContent());
        }
    }


    @Nested
    class 리뷰_조회 {

        @Test
        void 리뷰를_성공적으로_조회한다() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            ReviewResponse reviewResponse = reviewService.getReview(1L);

            assertThat(reviewResponse.getReviewerName()).isEqualTo(review.getReviewer().getName());
            assertThat(reviewResponse.getReviewedName()).isEqualTo(review.getReviewed().getName());
            assertThat(reviewResponse.getStarRating()).isEqualTo(review.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(review.getContent());
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> reviewService.getReview(1L));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class 리뷰_수정 {

        @Test
        void 성공적으로_리뷰를_수정한다() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            ReviewRequest updateRequest = new ReviewRequest(review.getReviewer(), review.getReviewed(), 4, "Good service");
            ReviewResponse reviewResponse = reviewService.updateReview(1L, updateRequest);

            assertThat(reviewResponse.getReviewerName()).isEqualTo(updateRequest.getReviewer().getName());
            assertThat(reviewResponse.getReviewedName()).isEqualTo(updateRequest.getReviewed().getName());
            assertThat(reviewResponse.getStarRating()).isEqualTo(updateRequest.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(updateRequest.getContent());
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            ReviewRequest updateRequest = new ReviewRequest(review.getReviewer(), review.getReviewed(), 4, "Good service");

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> reviewService.updateReview(1L, updateRequest));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class 리뷰_삭제 {

        @Test
        void 성공적으로_리뷰를_삭제한다() {
            when(reviewRepository.existsById(1L)).thenReturn(true);
            doNothing().when(reviewRepository).deleteById(1L);

            reviewService.deleteReview(1L);

            verify(reviewRepository, times(1)).deleteById(1L);
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            when(reviewRepository.existsById(1L)).thenReturn(false);

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> reviewService.deleteReview(1L));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class 상위_간병인_조회 {

        @Test
        void 성공적으로_상위_간병인을_조회한다() {
            List<Caregiver> caregivers = Arrays.asList(
                    caregiver,
                    Caregiver.builder().id(2L).name("Caregiver B").address(new Address("12345", "Seoul", "Gangnam")).build()
            );

            List<Review> reviewsForA = Arrays.asList(
                    Review.builder().reviewer(createMember(1L, "john_doe", "John Doe")).reviewed(caregiver).starRating(5).content("Great!").build(),
                    Review.builder().reviewer(createMember(2L, "jane_doe", "Jane Doe")).reviewed(caregiver).starRating(4).content("Good").build()
            );

            List<Review> reviewsForB = Arrays.asList(
                    Review.builder().reviewer(createMember(3L, "tom_cat", "Tom")).reviewed(createMember(2L, "caregiver_b", "Caregiver B")).starRating(3).content("Okay").build(),
                    Review.builder().reviewer(createMember(4L, "jerry_mouse", "Jerry")).reviewed(createMember(2L, "caregiver_b", "Caregiver B")).starRating(2).content("Not good").build()
            );

            when(caregiverRepository.findByRegion("Seoul")).thenReturn(caregivers);
            when(reviewRepository.findByReviewedName("Caregiver A")).thenReturn(reviewsForA);
            when(reviewRepository.findByReviewedName("Caregiver B")).thenReturn(reviewsForB);

            List<CaregiverRankingResponse> rankingResponses = reviewService.getTopCaregiversByRating("Seoul");

            assertThat(rankingResponses).hasSize(2);
            assertThat(rankingResponses.get(0).getName()).isEqualTo("Caregiver A");
            assertThat(rankingResponses.get(1).getName()).isEqualTo("Caregiver B");
        }
    }
}
