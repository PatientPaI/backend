package com.patientpal.backend.review.controller;

import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.defaultCaregiver;
import static com.patientpal.backend.fixtures.member.MemberFixture.defaultRoleCaregiver;
import static com.patientpal.backend.fixtures.member.MemberFixture.defaultRolePatient;
import static com.patientpal.backend.fixtures.review.ReviewsFixture.createReviewRequest;
import static com.patientpal.backend.fixtures.review.ReviewsFixture.createReviewResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.response.CaregiverRankingResponse;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.review.dto.ReviewRequest;
import com.patientpal.backend.review.dto.ReviewResponse;
import com.patientpal.backend.review.service.ReviewService;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;

@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
class ReviewsControllerTest extends CommonControllerSliceTest {

    @Autowired
    private ReviewService reviewService;

    private Member reviewer = defaultRolePatient();
    private Caregiver reviewed = defaultCaregiver();

    @Nested
    class 리뷰_생성 {

        @Test
        @WithMockUser
        void 성공한다() throws Exception {
            //given
            ReviewRequest reviewRequest = createReviewRequest(reviewed);
            ReviewResponse reviewResponse = createReviewResponse();
            String token = "valid-token";

            when(reviewService.createReview(any(ReviewRequest.class),anyString())).thenReturn(reviewResponse);

            //when & then
            mockMvc.perform(post("/api/v1/reviews")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(reviewRequest)))
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
    }


    @Nested
    class 리뷰_수정 {

        @Test
        @WithMockUser
        void 성공한다() throws Exception{
            //given
            ReviewRequest reviewRequest = createReviewRequest(reviewed);
            ReviewResponse reviewResponse = createReviewResponse();
            String token = "valid-token";

            when(reviewService.updateReview(eq(1L), any(ReviewRequest.class), anyString())).thenReturn(reviewResponse);

            //when & then
            mockMvc.perform(put("/api/v1/reviews/1")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(reviewRequest)))
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
