package com.patientpal.backend.patient.controller;

import static com.patientpal.backend.fixtures.patient.PatientFixture.createPatientProfileRequest;
import static com.patientpal.backend.fixtures.patient.PatientFixture.createPatientProfileResponse;
import static com.patientpal.backend.fixtures.patient.PatientFixture.updatePatientProfileRequest;
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

import com.patientpal.backend.common.custommockuser.WithCustomMockUserPatient;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.service.PatientService;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
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
            PatientProfileResponse response = createPatientProfileResponse();
            given(patientService.savePatientProfile(any(String.class), any(PatientProfileCreateRequest.class))).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/patient")
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
        @WithCustomMockUserPatient
        void 잘못된_요청이면_예외가_발생한다() throws Exception {
            // given
            PatientProfileCreateRequest request = new PatientProfileCreateRequest("", "", "", null, "", "", "", "");

            // when & then
            mockMvc.perform(post("/api/v1/patient")
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
            PatientProfileResponse response = createPatientProfileResponse();
            given(patientService.getProfile(any(String.class))).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/patient"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(response.getName()))
                    .andExpect(jsonPath("$.residentRegistrationNumber").value(response.getResidentRegistrationNumber()))
                    .andExpect(jsonPath("$.phoneNumber").value(response.getPhoneNumber()))
                    .andExpect(jsonPath("$.address.street").value(response.getAddress().getStreet()));
        }

        @Test
        @WithCustomMockUserPatient
        void 프로필이_없으면_예외가_발생한다() throws Exception {
            // given
            given(patientService.getProfile(any(String.class))).willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));

            // when & then
            mockMvc.perform(get("/api/v1/patient"))
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
            willDoNothing().given(patientService).updatePatientProfile(any(String.class), any(PatientProfileUpdateRequest.class));

            // when & then
            mockMvc.perform(patch("/api/v1/patient")
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
            willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST)).given(patientService).updatePatientProfile(any(String.class), any(PatientProfileUpdateRequest.class));

            // when & then
            mockMvc.perform(patch("/api/v1/patient")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 환자_프로필_삭제시에 {

        @Test
        @WithCustomMockUserPatient
        void 성공한다() throws Exception {
            // given
            willDoNothing().given(patientService).deletePatientProfile(any(String.class));

            // when & then
            mockMvc.perform(delete("/api/v1/patient"))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithCustomMockUserPatient
        void 프로필이_없으면_예외가_발생한다() throws Exception {
            // given
            willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST)).given(patientService).deletePatientProfile(any(String.class));

            // when & then
            mockMvc.perform(delete("/api/v1/patient"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
