package com.patientpal.backend.review.service;

import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.*;
import static com.patientpal.backend.fixtures.match.MatchFixture.*;
import static com.patientpal.backend.fixtures.member.MemberFixture.*;
import static com.patientpal.backend.fixtures.review.ReviewsFixture.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.review.domain.Reviews;
import com.patientpal.backend.review.dto.CreateReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.dto.UpdateReviewRequest;
import com.patientpal.backend.review.repository.ReviewRepository;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;

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
    private CreateReviewRequest createReviewRequest;
    private UpdateReviewRequest updateReviewRequest;

    private Match match;

    private Member reviewer;
    private Caregiver reviewed;



    @BeforeEach
    void setUp() {
        reviewer = defaultRolePatient();
        reviewed = defaultCaregiver();
        caregiver = defaultCaregiver();
        match = createMatchForPatient(defaultRolePatient(),defaultRoleCaregiver());
        reviews = createReview(reviewer, reviewed);
        createReviewRequest = createCreateReviewRequest(reviewed, match.getId());
        updateReviewRequest = createUpdateReviewRequest(reviewed);
    }

    @Nested
    class 리뷰_생성 {

        @Test
        void 성공적으로_리뷰를_생성한다() {
            String token = "valid-token";
            String username = reviewer.getUsername();
            String reviewedName = reviewed.getName();
            Long matchingId = match.getId();

            match.setMatchStatus(MatchStatus.COMPLETED);

            when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            when(caregiverRepository.findByUsername(reviewedName)).thenReturn(Optional.ofNullable(reviewed));
            when(matchRepository.findById(matchingId)).thenReturn(Optional.of(match));
            when(reviewRepository.findByReviewerIdAndReviewedId(reviewer.getId(), reviewed.getId())).thenReturn(Optional.empty());
            when(reviewRepository.save(any(Reviews.class))).thenReturn(reviews);

            CreateReviewRequest createReviewRequest = createCreateReviewRequest(reviewed, matchingId);

            ReviewResponse reviewResponse = reviewService.createReview(createReviewRequest, token);

            // then
            assertNotNull(reviewResponse);
            assertEquals(reviews.getId(), reviewResponse.getId());
            assertEquals(reviews.getReviewer().getId(), reviewResponse.getReviewerId());
            assertEquals(reviews.getReviewed().getId(), reviewResponse.getReviewedId());
            assertEquals(reviews.getStarRating(), reviewResponse.getStarRating());
            assertEquals(reviews.getContent(), reviewResponse.getContent());

            verify(reviewRepository, times(1)).save(any(Reviews.class));
            verify(memberRepository, times(1)).findByUsernameOrThrow(username);
            verify(caregiverRepository, times(1)).findByUsername(reviewedName);
            verify(matchRepository, times(1)).findById(matchingId);
        }

        @Test
        void 매칭이_완료되지_않은_경우_예외를_던진다() {
            // given
            String token = "valid-token";
            String username = reviewer.getUsername();
            Long matchingId = match.getId();
            String reviewedName = reviewed.getName();


            when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            when(caregiverRepository.findByUsername(reviewedName)).thenReturn(Optional.ofNullable(reviewed));
            when(matchRepository.findById(matchingId)).thenReturn(Optional.of(match));
            match.setMatchStatus(MatchStatus.PENDING);

            CreateReviewRequest createReviewRequest = createCreateReviewRequest(reviewed, matchingId);

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                reviewService.createReview(createReviewRequest, token);
            });

            assertEquals("리뷰를 작성할 수 없습니다. 매칭이 완료되지 않았습니다.", exception.getMessage());
        }

        @Test
        void 이미_작성된_리뷰가_있는_경우_예외를_던진다() {
            // given
            String token = "valid-token";
            String username = reviewer.getUsername();
            Long matchingId = match.getId();

            // 이미 작성된 리뷰가 있는 경우
            when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            when(caregiverRepository.findByUsername(reviewed.getName())).thenReturn(Optional.ofNullable(reviewed));
            when(reviewRepository.findByReviewerIdAndReviewedId(reviewer.getId(), reviewed.getId())).thenReturn(Optional.of(reviews));

            CreateReviewRequest createReviewRequest = createCreateReviewRequest(reviewed, matchingId);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(createReviewRequest, token));
        }
    }


    @Nested
    class 리뷰_조회 {

        @Test
        void 리뷰를_성공적으로_조회한다() {
            //given
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviews));

            //when
            ReviewResponse reviewResponse = reviewService.getReview(1L);

            //then
            assertThat(reviewResponse.getReviewerName()).isEqualTo(reviews.getReviewer().getName());
            assertThat(reviewResponse.getReviewedName()).isEqualTo(reviews.getReviewed().getName());
            assertThat(reviewResponse.getStarRating()).isEqualTo(reviews.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(reviews.getContent());
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            //given
            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            //when
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> reviewService.getReview(1L));

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }


        @Test
        void 성공적으로_모든_리뷰를_조회한다() {
            //given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Reviews> reviewsPage = new PageImpl<>(List.of(reviews), pageable, 1);

            when(reviewRepository.findAll(pageable)).thenReturn(reviewsPage);

            //when
            Page<ReviewResponse> result = reviewService.getAllReviews(pageable);

            //then
            assertThat(result.getContent().size()).isEqualTo(1);
            assertThat(result.getContent().get(0).getStarRating()).isEqualTo(reviews.getStarRating());
            assertThat(result.getContent().get(0).getContent()).isEqualTo(reviews.getContent());
        }

        @Test
        void 성공적으로_작성한_리뷰를_조회한다() {
            //given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Reviews> reviewsPage = new PageImpl<>(List.of(reviews), pageable, 1);

            when(memberRepository.findByUsernameOrThrow(reviewer.getUsername())).thenReturn(reviewer);
            when(reviewRepository.findByReviewerId(reviewer.getId(), pageable)).thenReturn(reviewsPage);

            //when
            Page<ReviewResponse> result = reviewService.getReviewsWrittenByUser(reviewer.getUsername(), pageable);

            //then
            assertThat(result.getContent().size()).isEqualTo(1);
            assertThat(result.getContent().get(0).getStarRating()).isEqualTo(reviews.getStarRating());
            assertThat(result.getContent().get(0).getContent()).isEqualTo(reviews.getContent());
        }

        @Test
        void 성공적으로_받은_리뷰를_조회한다() {
            //given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Reviews> reviewsPage = new PageImpl<>(List.of(reviews), pageable, 1);

            when(caregiverRepository.findByUsername(reviewed.getUsername())).thenReturn(Optional.ofNullable(reviewed));
            when(reviewRepository.findByReviewedId(reviewed.getId(), pageable)).thenReturn(reviewsPage);

            //when
            Page<ReviewResponse> result = reviewService.getReviewsReceivedByUser(reviewed.getUsername(), pageable);

            //then
            assertThat(result.getContent().size()).isEqualTo(1);
            assertThat(result.getContent().get(0).getStarRating()).isEqualTo(reviews.getStarRating());
            assertThat(result.getContent().get(0).getContent()).isEqualTo(reviews.getContent());
        }
    }

    @Nested
    class 리뷰_수정 {

        @Test
        void 성공적으로_리뷰를_수정한다() {
            //given
            String token = "valid-token";
            String username = reviewer.getUsername();
            String reviewedName = reviewed.getName();

            lenient().when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            lenient().when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            lenient().when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviews));

            //when
            UpdateReviewRequest updateRequest = new UpdateReviewRequest(reviewedName, 4, "Good service");
            ReviewResponse reviewResponse = reviewService.updateReview(1L, updateRequest, username);

            //then
            assertThat(reviewResponse.getStarRating()).isEqualTo(updateRequest.getStarRating());
            assertThat(reviewResponse.getContent()).isEqualTo(updateRequest.getContent());
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            //given
            String token = "valid-token";
            String username = reviewer.getUsername();
            String reviewedName = reviewed.getName();

            lenient().when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            lenient().when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            lenient().when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            UpdateReviewRequest updateRequest = new UpdateReviewRequest(reviewedName, 4, "Good service");

            //when
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> reviewService.updateReview(1L, updateRequest, token));

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class 리뷰_삭제 {

        @Test
        void 성공적으로_리뷰를_삭제한다() {
            //given
            String username = reviewer.getUsername();

            when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviews));
            doNothing().when(reviewRepository).deleteById(1L);

            //when
            reviewService.deleteReview(1L, username);

            //then
            verify(reviewRepository, times(1)).deleteById(1L);
        }

        @Test
        void 리뷰가_존재하지_않으면_예외가_발생한다() {
            //given
            String username = reviewer.getUsername();

            lenient().when(memberRepository.findByUsernameOrThrow(username)).thenReturn(reviewer);
            lenient().when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            //when
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                reviewService.deleteReview(1L, username);
            });

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class 상위_간병인_조회 {

        @Test
        void 성공적으로_상위_간병인을_조회한다() {
            //given
            Caregiver caregiverA = Caregiver.builder()
                    .id(1L)
                    .name("간병 A")
                    .address(new Address("12345", "서울 강남구", "Gangnam"))
                    .receivedReviews(Arrays.asList(
                            Reviews.builder().reviewer(createDefaultMember()).reviewed(caregiver).starRating(5).content("Great!").build(),
                            Reviews.builder().reviewer(createDefaultMember()).reviewed(caregiver).starRating(4).content("Good").build()
                    ))
                    .build();

            Caregiver caregiverB = Caregiver.builder()
                    .id(2L)
                    .name("간병 B")
                    .address(new Address("12345", "서울 관악구", "Gwanak"))
                    .receivedReviews(Arrays.asList(
                            Reviews.builder().reviewer(createDefaultMember()).reviewed(caregiver).starRating(3).content("Okay").build(),
                            Reviews.builder().reviewer(createDefaultMember()).reviewed(caregiver).starRating(2).content("Not good").build()
                    ))
                    .build();

            Caregiver caregiverC = Caregiver.builder()
                    .id(3L)
                    .name("간병 C")
                    .address(new Address("12345", "경기도 김포시", "GimPo"))
                    .receivedReviews(Arrays.asList(
                            Reviews.builder().reviewer(createDefaultMember()).reviewed(caregiver).starRating(4).content("Okay").build(),
                            Reviews.builder().reviewer(createDefaultMember()).reviewed(caregiver).starRating(3).content("Not good").build()
                    ))
                    .build();

            when(caregiverRepository.findByRegion("서울")).thenReturn(Arrays.asList(caregiverA, caregiverB));

            //when
            List<CaregiverRankingResponse> rankingResponses = reviewService.getTopCaregiversByRating("서울");

            //then
            assertThat(rankingResponses).hasSize(2);
            assertThat(rankingResponses.get(0).getName()).isEqualTo("간병 A");
            assertThat(rankingResponses.get(1).getName()).isEqualTo("간병 B");
        }
    }
}
