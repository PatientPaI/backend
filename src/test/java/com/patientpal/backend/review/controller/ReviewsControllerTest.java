package com.patientpal.backend.review.controller;

import static com.patientpal.backend.fixtures.member.MemberFixture.*;
import static com.patientpal.backend.fixtures.review.ReviewsFixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.fixtures.review.ReviewsFixture;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.review.dto.CreateReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.dto.UpdateReviewRequest;
import com.patientpal.backend.review.service.ReviewService;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;

@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
class ReviewsControllerTest extends CommonControllerSliceTest {

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private MemberRepository memberRepository;

    private Member reviewed = defaultRoleCaregiver();

    @Nested
    class 리뷰_생성 {

        @Test
        @WithMockUser
        void 성공한다() throws Exception {
            //given

            ReviewResponse reviewResponse = createReviewResponse();
            Long matchingId = 1L;
            CreateReviewRequest createReviewRequest = createCreateReviewRequest(reviewed, matchingId);
            String token = "valid-token";

            when(reviewService.createReview(any(CreateReviewRequest.class),anyString())).thenReturn(reviewResponse);

            //when & then
            mockMvc.perform(post("/api/v1/reviews")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createReviewRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.reviewerId").value(1L))
                    .andExpect(jsonPath("$.reviewerName").value("John Doe"))
                    .andExpect(jsonPath("$.reviewedId").value(2L))
                    .andExpect(jsonPath("$.reviewedName").value("Caregiver A"))
                    .andExpect(jsonPath("$.starRating").value(5))
                    .andExpect(jsonPath("$.content").value("Excellent service"));
        }
    }

    @Nested
    class 리뷰_조회 {
        @Test
        @WithMockUser
        void 성공한다() throws Exception{
            //given
            ReviewResponse reviewResponse = createReviewResponse();

            when(reviewService.getReview(1L)).thenReturn(reviewResponse);

            //when & then
            mockMvc.perform(get("/api/v1/reviews/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.reviewerId").value(1L))
                    .andExpect(jsonPath("$.reviewerName").value("John Doe"))
                    .andExpect(jsonPath("$.reviewedId").value(2L))
                    .andExpect(jsonPath("$.reviewedName").value("Caregiver A"))
                    .andExpect(jsonPath("$.starRating").value(5))
                    .andExpect(jsonPath("$.content").value("Excellent service"));
        }

        @Test
        void 전체_리뷰_조회() throws Exception {
            //given
            Pageable pageable = PageRequest.of(0, 10);
            ReviewResponse reviewResponse = ReviewsFixture.createReviewResponse();

            Page<ReviewResponse> reviewPage = new PageImpl<>(Collections.singletonList(reviewResponse), pageable, 1);

            when(reviewService.getAllReviews(any(Pageable.class))).thenReturn(reviewPage);

            //when & then
            mockMvc.perform(get("/api/v1/reviews")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"content\":[{\"id\":1,\"reviewerId\":1,\"reviewerName\":\"John Doe\",\"reviewedId\":2,\"reviewedName\":\"Caregiver A\",\"starRating\":5,\"content\":\"Excellent service\"}],\"pageable\":{\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"pageNumber\":0,\"pageSize\":10,\"offset\":0,\"paged\":true,\"unpaged\":false},\"totalPages\":1,\"totalElements\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"first\":true,\"numberOfElements\":1,\"empty\":false}"));
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void 내가_작성한_리뷰_조회() throws Exception {
            //given
            Pageable pageable = PageRequest.of(0, 10);
            ReviewResponse reviewResponse = ReviewsFixture.createReviewResponse();

            Page<ReviewResponse> reviewPage = new PageImpl<>(Collections.singletonList(reviewResponse), pageable, 1);

            when(reviewService.getReviewsWrittenByUser(any(String.class), any(Pageable.class))).thenReturn(reviewPage);

            //when & then
            mockMvc.perform(get("/api/v1/reviews/written")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"content\":[{\"id\":1,\"reviewerId\":1,\"reviewerName\":\"John Doe\",\"reviewedId\":2,\"reviewedName\":\"Caregiver A\",\"starRating\":5,\"content\":\"Excellent service\"}],\"totalPages\":1,\"totalElements\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"first\":true,\"numberOfElements\":1,\"empty\":false}", false));
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void 내가_받은_리뷰_조회() throws Exception {
            //given
            Pageable pageable = PageRequest.of(0, 10);
            ReviewResponse reviewResponse = ReviewsFixture.createReviewResponse();

            Page<ReviewResponse> reviewPage = new PageImpl<>(Collections.singletonList(reviewResponse), pageable, 1);

            when(reviewService.getReviewsReceivedByUser(any(String.class), any(Pageable.class))).thenReturn(reviewPage);

            //when & then
            mockMvc.perform(get("/api/v1/reviews/received")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"content\":[{\"id\":1,\"reviewerId\":1,\"reviewerName\":\"John Doe\",\"reviewedId\":2,\"reviewedName\":\"Caregiver A\",\"starRating\":5,\"content\":\"Excellent service\"}],\"totalPages\":1,\"totalElements\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"first\":true,\"numberOfElements\":1,\"empty\":false}", false));
        }
    }



    @Nested
    class 리뷰_수정 {

        @Test
        @WithMockUser
        void 성공한다() throws Exception{
            //given
            UpdateReviewRequest updateReviewRequest = createUpdateReviewRequest(reviewed);
            ReviewResponse reviewResponse = createReviewResponse();
            String token = "valid-token";

            when(reviewService.updateReview(eq(1L), any(UpdateReviewRequest.class), anyString())).thenReturn(reviewResponse);

            //when & then
            mockMvc.perform(put("/api/v1/reviews/1")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateReviewRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.reviewerId").value(1L))
                    .andExpect(jsonPath("$.reviewerName").value("John Doe"))
                    .andExpect(jsonPath("$.reviewedId").value(2L))
                    .andExpect(jsonPath("$.reviewedName").value("Caregiver A"))
                    .andExpect(jsonPath("$.starRating").value(5))
                    .andExpect(jsonPath("$.content").value("Excellent service"));
        }
    }

    @Nested
    class 리뷰_삭제 {

        @Test
        @WithMockUser
        public void 리뷰가_존재하면_삭제된다() throws Exception{
            //given
            String token = "valid-token";
            doNothing().when(reviewService).deleteReview(eq(1L), anyString());

            // when & then
            mockMvc.perform(delete("/api/v1/reviews/1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser
        void 리뷰가_존재하지_않으면_예외가_발생한다() throws Exception {
            // given
            String token = "valid-token";
            doThrow(new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND)).when(reviewService).deleteReview(eq(1L), anyString());

            // when & then
            mockMvc.perform(delete("/api/v1/reviews/1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 상위_간병인_조회 {

        @Test
        @WithMockUser
        void 성공적으로_상위_간병인을_조회한다() throws Exception {
            // given
            List<CaregiverRankingResponse> rankingResponses = Arrays.asList(
                    new CaregiverRankingResponse(1L, "Caregiver A", "Seoul", 4.8F),
                    new CaregiverRankingResponse(2L, "Caregiver B", "Seoul", 4.5F)
            );

            when(reviewService.getTopCaregiversByRating("Seoul")).thenReturn(rankingResponses);

            // when & then
            mockMvc.perform(get("/api/v1/reviews/top-caregivers")
                            .param("region", "Seoul")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].name").value("Caregiver A"))
                    .andExpect(jsonPath("$[0].rating").value(4.8))
                    .andExpect(jsonPath("$[1].name").value("Caregiver B"))
                    .andExpect(jsonPath("$[1].rating").value(4.5));
        }
    }
}
