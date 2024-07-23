package com.patientpal.backend.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CaregiverRepository caregiverRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Caregiver caregiver;
    private Review review;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        caregiver = Caregiver.builder()
                .id(1L)
                .name("Caregiver A")
                .address(new Address("12345", "Seoul", "Gangnam"))
                .build();


        review = Review.builder()
                .reviewerName("John Doe")
                .reviewedName("Caregiver A")
                .starRating(5)
                .content("Excellent service")
                .build();

        reviewRequest = ReviewRequest.builder()
                .reviewerName("John Doe")
                .reviewedName("Caregiver A")
                .starRating(5)
                .content("Excellent service")
                .build();
    }

    @Nested
    class 리뷰_생성 {

        @Test
        void 성공적으로_리뷰를_생성한다() {
            when(reviewRepository.save(any(Review.class))).thenReturn(review);

            ReviewResponse reviewResponse = reviewService.createReview(reviewRequest);

            assertThat(reviewResponse.getReviewerName()).isEqualTo(review.getReviewerName());
            assertThat(reviewResponse.getReviewedName()).isEqualTo(review.getReviewedName());
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

            assertThat(reviewResponse.getReviewerName()).isEqualTo(review.getReviewerName());
            assertThat(reviewResponse.getReviewedName()).isEqualTo(review.getReviewedName());
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

            ReviewRequest updateRequest = new ReviewRequest("John Doe", "Caregiver A", 4, "Good service");
            ReviewResponse reviewResponse = reviewService.updateReview(1L, updateRequest);

            assertThat(reviewResponse.getReviewerName()).isEqualTo(updateRequest.getReviewerName());
            assertThat(reviewResponse.getReviewedName()).isEqualTo(updateRequest.getReviewedName());
            assertThat(reviewResponse.getStarRating()).isEqualTo(updateRequest.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(updateRequest.getContent());
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            ReviewRequest updateRequest = new ReviewRequest("John Doe", "Caregiver A", 4, "Good service");

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
                    Review.builder().reviewerName("John").reviewedName("Caregiver A").starRating(5).content("Great!").build(),
                    Review.builder().reviewerName("Jane").reviewedName("Caregiver A").starRating(4).content("Good").build()
            );

            List<Review> reviewsForB = Arrays.asList(
                    Review.builder().reviewerName("Tom").reviewedName("Caregiver B").starRating(3).content("Okay").build(),
                    Review.builder().reviewerName("Jerry").reviewedName("Caregiver B").starRating(2).content("Not good").build()
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
