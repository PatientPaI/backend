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
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.service.CaregiverService;
import com.patientpal.backend.common.custommockuser.WithCustomMockUserCaregiver;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.test.CommonControllerSliceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
class CaregiverControllerV1Test extends CommonControllerSliceTest {

    @Autowired
    CaregiverService caregiverService;

    @Nested
    class 간병인_프로필_생성 {

        @Test
        @DisplayName("간병인 프로필을 성공적으로 생성한다.")
        @WithCustomMockUserCaregiver
        void successCreateCaregiverProfile() throws Exception {
            // given
            CaregiverProfileCreateRequest request = createCaregiverProfileRequest();
            CaregiverProfileResponse response = createCaregiverProfileResponse();
            given(caregiverService.saveCaregiverProfile(any(String.class), any(CaregiverProfileCreateRequest.class))).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/caregiver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(response.getName()))
                    .andExpect(jsonPath("$.residentRegistrationNumber").value(response.getResidentRegistrationNumber()))
                    .andExpect(jsonPath("$.phoneNumber").value(response.getPhoneNumber()))
                    .andExpect(jsonPath("$.address.street").value(response.getAddress().getStreet()));
        }

        @Test
        @DisplayName("간병인 프로필을 생성할 때 잘못된 요청이면 예외가 발생한다.")
        @WithCustomMockUserCaregiver
        void failCreateCaregiverProfileBadRequest() throws Exception {
            // given
            CaregiverProfileCreateRequest request = new CaregiverProfileCreateRequest("", "", "", null, 0, 1, "", "");

            // when & then
            mockMvc.perform(post("/api/v1/caregiver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class 간병인_프로필_조회 {

        @Test
        @DisplayName("간병인 프로필을 성공적으로 조회한다.")
        @WithCustomMockUserCaregiver
        void successGetCaregiverProfile() throws Exception {
            // given
            CaregiverProfileResponse response = createCaregiverProfileResponse();
            given(caregiverService.getProfile(any(String.class))).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/caregiver"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(response.getName()))
                    .andExpect(jsonPath("$.residentRegistrationNumber").value(response.getResidentRegistrationNumber()))
                    .andExpect(jsonPath("$.phoneNumber").value(response.getPhoneNumber()))
                    .andExpect(jsonPath("$.address.street").value(response.getAddress().getStreet()));
        }

        @Test
        @DisplayName("간병인 프로필을 조회할 때 프로필이 없으면 예외가 발생한다.")
        @WithCustomMockUserCaregiver
        void failGetCaregiverProfileNotFound() throws Exception {
            // given
            given(caregiverService.getProfile(any(String.class))).willThrow(new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));

            // when & then
            mockMvc.perform(get("/api/v1/caregiver"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 간병인_프로필_수정 {

        @Test
        @DisplayName("간병인 프로필을 성공적으로 수정한다.")
        @WithCustomMockUserCaregiver
        void successUpdateCaregiverProfile() throws Exception {
            // given
            CaregiverProfileUpdateRequest request = updateCaregiverProfileRequest();
            willDoNothing().given(caregiverService).updateCaregiverProfile(any(String.class), any(CaregiverProfileUpdateRequest.class));

            // when & then
            mockMvc.perform(patch("/api/v1/caregiver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("간병인 프로필을 수정할 때 프로필이 없으면 예외가 발생한다.")
        @WithCustomMockUserCaregiver
        void failUpdateCaregiverProfileNotFound() throws Exception {
            // given
            CaregiverProfileUpdateRequest request = updateCaregiverProfileRequest();
            willThrow(new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST)).given(caregiverService).updateCaregiverProfile(any(String.class), any(CaregiverProfileUpdateRequest.class));

            // when & then
            mockMvc.perform(patch("/api/v1/caregiver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 간병인_프로필_삭제 {

        @Test
        @DisplayName("간병인 프로필을 성공적으로 삭제한다.")
        @WithCustomMockUserCaregiver
        void successDeleteCaregiverProfile() throws Exception {
            // given
            willDoNothing().given(caregiverService).deleteCaregiverProfile(any(String.class));

            // when & then
            mockMvc.perform(delete("/api/v1/caregiver"))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("간병인 프로필을 삭제할 때 프로필이 없으면 예외가 발생한다.")
        @WithCustomMockUserCaregiver
        void failDeleteCaregiverProfileNotFound() throws Exception {
            // given
            willThrow(new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST)).given(caregiverService).deleteCaregiverProfile(any(String.class));

            // when & then
            mockMvc.perform(delete("/api/v1/caregiver"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
