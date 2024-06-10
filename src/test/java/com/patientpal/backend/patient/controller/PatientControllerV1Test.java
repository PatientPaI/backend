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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class PatientControllerV1Test extends CommonControllerSliceTest {

    @Autowired
    private PatientService patientService;

    @Nested
    class 환자_프로필_생성 {

        @Test
        @DisplayName("환자 프로필을 성공적으로 생성한다.")
        @WithCustomMockUserPatient
        void successCreatePatientProfile() throws Exception {
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
        @DisplayName("환자 프로필을 생성할 때 잘못된 요청이면 예외가 발생한다.")
        @WithCustomMockUserPatient
        void failCreatePatientProfileBadRequest() throws Exception {
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
    class 환자_프로필_조회 {

        @Test
        @DisplayName("환자 프로필을 성공적으로 조회한다.")
        @WithCustomMockUserPatient
        void successGetPatientProfile() throws Exception {
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
        @DisplayName("환자 프로필을 조회할 때 프로필이 없으면 예외가 발생한다.")
        @WithCustomMockUserPatient
        void failGetPatientProfileNotFound() throws Exception {
            // given
            given(patientService.getProfile(any(String.class))).willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));

            // when & then
            mockMvc.perform(get("/api/v1/patient"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class 환자_프로필_수정 {

        @Test
        @DisplayName("환자 프로필을 성공적으로 수정한다.")
        @WithCustomMockUserPatient
        void successUpdatePatientProfile() throws Exception {
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
        @DisplayName("환자 프로필을 수정할 때 프로필이 없으면 예외가 발생한다.")
        @WithCustomMockUserPatient
        void failUpdatePatientProfileNotFound() throws Exception {
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
    class 환자_프로필_삭제 {

        @Test
        @DisplayName("환자 프로필을 성공적으로 삭제한다.")
        @WithCustomMockUserPatient
        void successDeletePatientProfile() throws Exception {
            // given
            willDoNothing().given(patientService).deletePatientProfile(any(String.class));

            // when & then
            mockMvc.perform(delete("/api/v1/patient"))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("환자 프로필을 삭제할 때 프로필이 없으면 예외가 발생한다.")
        @WithCustomMockUserPatient
        void failDeletePatientProfileNotFound() throws Exception {
            // given
            willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST)).given(patientService).deletePatientProfile(any(String.class));

            // when & then
            mockMvc.perform(delete("/api/v1/patient"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
