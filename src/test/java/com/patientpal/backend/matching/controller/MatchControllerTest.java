package com.patientpal.backend.matching.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.matching.service.MatchService;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import com.patientpal.backend.test.annotation.WithCustomMockUserPatient;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
public class MatchControllerTest extends CommonControllerSliceTest {

    @Autowired
    private MatchService matchService;

    @Nested
    class 매칭_생성_환자_케이스 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            MatchResponse response = MatchResponse.builder().build();
            given(matchService.createMatch(any(String.class), any(Long.class))).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/matches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("responseMemberId", "1"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(response.getId()));
        }

        @Test
        @WithCustomMockUserPatient
        void 잘못된_요청이면_예외가_발생한다() throws Exception {
            // when & then
            mockMvc.perform(post("/api/v1/matches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("responseMemberId", "invalidId"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class 매칭_생성_간병인_케이스 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            MatchResponse response = MatchResponse.builder().build();

            given(matchService.createMatch(any(String.class), any(Long.class))).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/matches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("responseMemberId", "1"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(response.getId()));
        }

        @Test
        @WithCustomMockUserPatient
        void 잘못된_요청이면_예외가_발생한다() throws Exception {
            // when & then
            mockMvc.perform(post("/api/v1/matches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("responseMemberId", "invalidId"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class 매칭_조회_케이스 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            MatchResponse response = MatchResponse.builder().build();

            given(matchService.getMatch(any(Long.class), any(String.class))).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/matches/{matchId}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.getId()));
        }

        @Test
        @WithCustomMockUserPatient
        void 매칭_정보가_없으면_예외가_발생한다() throws Exception {
            // given
            given(matchService.getMatch(any(Long.class), any(String.class))).willThrow(
                    new EntityNotFoundException(ErrorCode.MATCH_NOT_EXIST));

            // when & then
            mockMvc.perform(get("/api/v1/matches/{matchId}", 1L))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 매칭_리스트_조회_요청_보낸_케이스 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            MatchResponse response1 = MatchResponse.builder().build();
            MatchResponse response2 = MatchResponse.builder().build();
            List<MatchResponse> responses = List.of(response1, response2);
            PageImpl<MatchResponse> matchPage = new PageImpl<>(responses, PageRequest.of(0, 10), responses.size());
            MatchListResponse listResponse = new MatchListResponse(responses, 0, matchPage.getTotalPages(),
                    matchPage.getTotalElements());
            given(matchService.getRequestMatches(any(String.class), any(), any())).willReturn(listResponse);

            // when & then
            mockMvc.perform(get("/api/v1/matches/{memberId}/all/request", 1L)
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class 매칭_리스트_조회_요청_받은_케이스 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            MatchResponse response1 = MatchResponse.builder().build();
            MatchResponse response2 = MatchResponse.builder().build();
            List<MatchResponse> responses = List.of(response1, response2);
            PageImpl<MatchResponse> matchPage = new PageImpl<>(responses, PageRequest.of(0, 10), responses.size());
            MatchListResponse listResponse = new MatchListResponse(responses, 0, matchPage.getTotalPages(),
                    matchPage.getTotalElements());
            given(matchService.getReceivedMatches(any(String.class), any(), any())).willReturn(listResponse);

            // when & then
            mockMvc.perform(get("/api/v1/matches/{memberId}/all/received", 1L)
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class 매칭_취소_케이스 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            willDoNothing().given(matchService).cancelMatch(any(Long.class), any(String.class));

            // when & then
            mockMvc.perform(post("/api/v1/matches/{matchId}/cancel", 1L))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithCustomMockUserPatient
        void 매칭_정보가_없으면_예외가_발생한다() throws Exception {
            // given
            willThrow(new EntityNotFoundException(ErrorCode.MATCH_NOT_EXIST)).given(matchService)
                    .cancelMatch(any(Long.class), any(String.class));

            // when & then
            mockMvc.perform(post("/api/v1/matches/{matchId}/cancel", 1L))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 매칭_수락_케이스 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            willDoNothing().given(matchService).acceptMatch(any(Long.class), any(String.class));

            // when & then
            mockMvc.perform(post("/api/v1/matches/{matchId}/accept", 1L))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithCustomMockUserPatient
        void 매칭_정보가_없으면_예외가_발생한다() throws Exception {
            // given
            willThrow(new EntityNotFoundException(ErrorCode.MATCH_NOT_EXIST)).given(matchService)
                    .acceptMatch(any(Long.class), any(String.class));

            // when & then
            mockMvc.perform(post("/api/v1/matches/{matchId}/accept", 1L))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
