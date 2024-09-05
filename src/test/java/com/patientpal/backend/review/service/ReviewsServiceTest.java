package com.patientpal.backend.review.service;

import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.defaultCaregiver;
import static com.patientpal.backend.fixtures.match.MatchFixture.createMatchForPatient;
import static com.patientpal.backend.fixtures.member.MemberFixture.createDefaultMember;
import static com.patientpal.backend.fixtures.member.MemberFixture.defaultRoleCaregiver;
import static com.patientpal.backend.fixtures.member.MemberFixture.defaultRolePatient;
import static com.patientpal.backend.fixtures.review.ReviewsFixture.createReview;
import static com.patientpal.backend.fixtures.review.ReviewsFixture.createReviewRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.review.domain.Reviews;
import com.patientpal.backend.review.dto.ReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.repository.ReviewRepository;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
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

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
class ReviewsServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ReviewService reviewService;

    private Caregiver caregiver;
    private Reviews reviews;
    private ReviewRequest reviewRequest;

    private Match match;

    private Member reviewer;
    private Member reviewed;



    @BeforeEach
    void setUp() {
        reviewer = defaultRolePatient();
        reviewed = defaultRoleCaregiver();
        caregiver = defaultCaregiver();
        match = createMatchForPatient(defaultRolePatient(),defaultRoleCaregiver());
        reviews = createReview(reviewer, reviewed);
        reviewRequest = createReviewRequest(reviewer, reviewed);
    }

    @Nested
    class 리뷰_생성 {

        @Test
        void 성공적으로_리뷰를_생성한다() {
            String token = "valid-token";
            String username = reviewer.getUsername();

            when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            when(matchRepository.findCompleteMatchForMember(reviewer.getId())).thenReturn(Optional.of(match));
            when(memberRepository.findById(reviewed.getId())).thenReturn(Optional.of(reviewed));
            when(reviewRepository.save(any(Reviews.class))).thenReturn(reviews);

            ReviewResponse reviewResponse = reviewService.createReview(reviewRequest, token);

            assertThat(reviewResponse.getStarRating()).isEqualTo(reviews.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(reviews.getContent());
        }
    }


    @Nested
    class 리뷰_조회 {

        @Test
        void 리뷰를_성공적으로_조회한다() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviews));

            ReviewResponse reviewResponse = reviewService.getReview(1L);

            assertThat(reviewResponse.getReviewerName()).isEqualTo(reviews.getReviewer().getName());
            assertThat(reviewResponse.getReviewedName()).isEqualTo(reviews.getReviewed().getName());
            assertThat(reviewResponse.getStarRating()).isEqualTo(reviews.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(reviews.getContent());
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
            String token = "valid-token";
            String username = reviewer.getUsername();

            lenient().when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            lenient().when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            lenient().when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviews));

            ReviewRequest updateRequest = new ReviewRequest(reviewer, reviewed, 4, "Good service");
            ReviewResponse reviewResponse = reviewService.updateReview(1L, updateRequest, username);

            assertThat(reviewResponse.getStarRating()).isEqualTo(updateRequest.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(updateRequest.getContent());
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            String token = "valid-token";
            String username = reviewer.getUsername();

            lenient().when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            lenient().when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            lenient().when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            ReviewRequest updateRequest = new ReviewRequest(reviewer, reviewed, 4, "Good service");

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> reviewService.updateReview(1L, updateRequest, token));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class 리뷰_삭제 {

        @Test
        void 성공적으로_리뷰를_삭제한다() {
            String username = reviewer.getUsername();

            when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviews));
            doNothing().when(reviewRepository).deleteById(1L);

            reviewService.deleteReview(1L, username);

            verify(reviewRepository, times(1)).deleteById(1L);
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            String username = reviewer.getUsername();

            lenient().when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            lenient().when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                reviewService.deleteReview(1L, username);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class 상위_간병인_조회 {

        @Test
        void 성공적으로_상위_간병인을_조회한다() {
            List<Caregiver> caregivers = Arrays.asList(
                    caregiver,
                    Caregiver.builder().id(2L).name("간병 B").address(new Address("12345", "Seoul", "Gangnam")).build()
            );

            List<Reviews> reviewsForA = Arrays.asList(
                    Reviews.builder().reviewer(createDefaultMember()).reviewed(caregiver).starRating(5).content("Great!").build(),
                    Reviews.builder().reviewer(createDefaultMember()).reviewed(caregiver).starRating(4).content("Good").build()
            );

            List<Reviews> reviewsForB = Arrays.asList(
                    Reviews.builder().reviewer(createDefaultMember()).reviewed(createDefaultMember()).starRating(3).content("Okay").build(),
                    Reviews.builder().reviewer(createDefaultMember()).reviewed(createDefaultMember()).starRating(2).content("Not good").build()
            );

            when(caregiverRepository.findByRegion("Seoul")).thenReturn(caregivers);
            lenient().when(reviewRepository.findByReviewedName("간병")).thenReturn(reviewsForA);
            lenient().when(reviewRepository.findByReviewedName("간병 B")).thenReturn(reviewsForB);

            List<CaregiverRankingResponse> rankingResponses = reviewService.getTopCaregiversByRating("Seoul");

            assertThat(rankingResponses).hasSize(2);
            assertThat(rankingResponses.get(0).getName()).isEqualTo("간병");
            assertThat(rankingResponses.get(1).getName()).isEqualTo("간병 B");
        }
    }
}
