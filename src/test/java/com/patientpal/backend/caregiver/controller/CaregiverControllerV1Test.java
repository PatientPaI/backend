package com.patientpal.backend.caregiver.controller;

import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.createCaregiverProfileRequest;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.createCaregiverProfileResponse;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.updateCaregiverProfileRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileDetailResponse;
import com.patientpal.backend.caregiver.service.CaregiverService;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import com.patientpal.backend.test.annotation.WithCustomMockUserCaregiver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
class CaregiverControllerV1Test extends CommonControllerSliceTest {

    @Autowired
    CaregiverService caregiverService;

    @Nested
    class 간병인_프로필_생성시에 {

        @Test
        @WithCustomMockUserCaregiver
        void 성공한다() throws Exception {
            // given
            CaregiverProfileCreateRequest request = createCaregiverProfileRequest();
            CaregiverProfileDetailResponse response = createCaregiverProfileResponse();
            given(caregiverService.saveCaregiverProfile(any(String.class), any(CaregiverProfileCreateRequest.class), any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/caregiver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(response.getName()))
                    .andExpect(jsonPath("$.residentRegistrationNumber").value(response.getResidentRegistrationNumber()))
                    .andExpect(jsonPath("$.phoneNumber").value(response.getPhoneNumber()));
        }

        @Test
        @WithCustomMockUserCaregiver
        void 잘못된_요청이면_예외가_발생한다() throws Exception {
            // given
            CaregiverProfileCreateRequest request = new CaregiverProfileCreateRequest("", "", "", null, new Address("hi", "ho", "ha"), 1, 1, "", "");

            // when & then
            mockMvc.perform(post("/api/v1/caregiver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class 간병인_프로필_조회시에 {

        @Test
        @WithCustomMockUserCaregiver
        void 성공한다() throws Exception {
            // given
            CaregiverProfileDetailResponse response = createCaregiverProfileResponse();
            given(caregiverService.getProfile(any(String.class))).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/caregiver"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(response.getName()))
                    .andExpect(jsonPath("$.residentRegistrationNumber").value(response.getResidentRegistrationNumber()))
                    .andExpect(jsonPath("$.phoneNumber").value(response.getPhoneNumber()));
        }

        @Test
        @WithCustomMockUserCaregiver
        void 프로필_미등록시에_예외가_발생한다() throws Exception {
            // given
            given(caregiverService.getProfile(any(String.class))).willThrow(new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));

            // when & then
            mockMvc.perform(get("/api/v1/caregiver"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 간병인_프로필_수정시에 {

        @Test
        @WithCustomMockUserCaregiver
        void 성공한다() throws Exception {
            // given
            CaregiverProfileUpdateRequest request = updateCaregiverProfileRequest();
            willDoNothing().given(caregiverService).updateCaregiverProfile(any(String.class), any(CaregiverProfileUpdateRequest.class), any());

            // when & then
            mockMvc.perform(patch("/api/v1/caregiver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithCustomMockUserCaregiver
        void 프로필_미등록시_예외가_발생한다() throws Exception {
            // given
            CaregiverProfileUpdateRequest request = updateCaregiverProfileRequest();
            willThrow(new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST)).given(caregiverService).updateCaregiverProfile(any(String.class), any(CaregiverProfileUpdateRequest.class), any());

            // when & then
            mockMvc.perform(patch("/api/v1/caregiver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 간병인_프로필_삭제시에 {

        @Test
        @WithCustomMockUserCaregiver
        void 성공한다() throws Exception {
            // given
            willDoNothing().given(caregiverService).deleteCaregiverProfile(any(String.class));

            // when & then
            mockMvc.perform(delete("/api/v1/caregiver"))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithCustomMockUserCaregiver
        void 프로필_미등록시_예외가_발생한다() throws Exception {
            // given
            willThrow(new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST)).given(caregiverService).deleteCaregiverProfile(any(String.class));

            // when & then
            mockMvc.perform(delete("/api/v1/caregiver"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
