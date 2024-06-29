package com.patientpal.backend.patient.controller;

import static com.patientpal.backend.fixtures.patient.PatientFixture.createPatientProfileRequest;
import static com.patientpal.backend.fixtures.patient.PatientFixture.createPatientProfileResponse;
import static com.patientpal.backend.fixtures.patient.PatientFixture.updatePatientProfileRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileDetailResponse;
import com.patientpal.backend.patient.service.PatientService;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import com.patientpal.backend.test.annotation.WithCustomMockUserCaregiver;
import com.patientpal.backend.test.annotation.WithCustomMockUserPatient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
public class PatientControllerV1Test extends CommonControllerSliceTest {

    @Autowired
    private PatientService patientService;

    @Nested
    class 환자_프로필_생성시에 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            PatientProfileCreateRequest request = createPatientProfileRequest();
            PatientProfileDetailResponse response = createPatientProfileResponse();
            given(patientService.savePatientProfile(any(String.class), any(PatientProfileCreateRequest.class), any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/patient/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(response.getName()))
                    .andExpect(jsonPath("$.residentRegistrationNumber").value(response.getResidentRegistrationNumber()))
                    .andExpect(jsonPath("$.contact").value(response.getContact()));
        }

        @Test
        @WithCustomMockUserPatient
        void 잘못된_요청이면_예외가_발생한다() throws Exception {
            // given
            PatientProfileCreateRequest request = new PatientProfileCreateRequest("", "", "", null, "", "", "", "",
                    Gender.MALE);

            // when & then
            mockMvc.perform(post("/api/v1/patient/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class 환자_프로필_조회시에 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            PatientProfileDetailResponse response = createPatientProfileResponse();
            given(patientService.getProfile(any(String.class), any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/patient/profile/{memberId}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(response.getName()))
                    .andExpect(jsonPath("$.residentRegistrationNumber").value(response.getResidentRegistrationNumber()))
                    .andExpect(jsonPath("$.contact").value(response.getContact()));
        }

        @Test
        @WithCustomMockUserPatient
        void 프로필이_없으면_예외가_발생한다() throws Exception {
            // given
            given(patientService.getProfile(any(String.class), any())).willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));

            // when & then
            mockMvc.perform(get("/api/v1/patient/profile/{memberId}", 1L))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 환자_프로필_수정시에 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            PatientProfileUpdateRequest request = updatePatientProfileRequest();
            willDoNothing().given(patientService).updatePatientProfile(any(String.class), any(), any(PatientProfileUpdateRequest.class), any());

            // when & then
            mockMvc.perform(patch("/api/v1/patient/profile/{memberId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithCustomMockUserPatient
        void 프로필이_없으면_예외가_발생한다() throws Exception {
            // given
            PatientProfileUpdateRequest request = updatePatientProfileRequest();
            willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST)).given(patientService).updatePatientProfile(any(String.class), any(), any(PatientProfileUpdateRequest.class), any());

            // when & then
            mockMvc.perform(patch("/api/v1/patient/profile/{memberId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 간병인_프로필_매칭_리스트에_등록_시에 {

        @Test
        @WithCustomMockUserCaregiver
        void 성공한다() throws Exception {
            // given
            willDoNothing().given(patientService).registerPatientProfileToMatchList(any(String.class), any(Long.class));

            // when & then
            mockMvc.perform(post("/api/v1/patient/profile/{memberId}/register/toMatchList", 1L))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithCustomMockUserCaregiver
        void 권한이_없으면_예외가_발생한다() throws Exception {
            // given
            willThrow(new BusinessException(ErrorCode.AUTHORIZATION_FAILED)).given(patientService).registerPatientProfileToMatchList(any(String.class), any(Long.class));

            // when & then
            mockMvc.perform(post("/api/v1/patient/profile/{memberId}/register/toMatchList", 1L))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class 간병인_프로필_매칭_리스트에_제거_시에 {

        @Test
        @WithCustomMockUserCaregiver
        void 성공한다() throws Exception {
            // given
            willDoNothing().given(patientService).unregisterPatientProfileToMatchList(any(String.class), any(Long.class));

            // when & then
            mockMvc.perform(post("/api/v1/patient/profile/{memberId}/unregister/toMatchList", 1L))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithCustomMockUserCaregiver
        void 권한이_없으면_예외가_발생한다() throws Exception {
            // given
            willThrow(new BusinessException(ErrorCode.AUTHORIZATION_FAILED)).given(patientService).unregisterPatientProfileToMatchList(any(String.class), any(Long.class));

            // when & then
            mockMvc.perform(post("/api/v1/patient/profile/{memberId}/unregister/toMatchList", 1L))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}

