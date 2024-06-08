package com.patientpal.backend.patient.controller;

import static com.patientpal.backend.common.setup.PatientSetUpCommon.setUpPatientProfileCreateRequest;
import static com.patientpal.backend.common.setup.PatientSetUpCommon.setUpPatientProfileResponse;
import static com.patientpal.backend.common.setup.PatientSetUpCommon.setUpPatientProfileUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.service.PatientService;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(PatientControllerV1.class)
public class PatientControllerV1Test {

    @MockBean
    private PatientService patientService;

    @MockBean
    private MemberService memberService;

    @InjectMocks
    private PatientControllerV1 patientController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        given(jwtTokenProvider.getUsernameFromToken(any())).willReturn("1");
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("환자 프로필을 성공적으로 생성한다.")
    @WithMockUser(username = "lhs")
    void successCreatePatientProfile() throws Exception {
        // given
        PatientProfileCreateRequest request = setUpPatientProfileCreateRequest();
        PatientProfileResponse response = setUpPatientProfileResponse();
        given(patientService.savePatientProfile(any(String.class), any(PatientProfileCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("환자 프로필을 성공적으로 조회한다.")
    @WithMockUser(username = "lhs")
    void successGetPatientProfile() throws Exception {
        // given
        PatientProfileResponse response = setUpPatientProfileResponse();
        given(patientService.getProfile(any(String.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/patient"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("환자 프로필을 성공적으로 수정한다.")
    @WithMockUser(username = "lhs")
    void successUpdatePatientProfile() throws Exception {
        // given
        PatientProfileUpdateRequest request = setUpPatientProfileUpdateRequest();
        willDoNothing().given(patientService).updatePatientProfile(any(String.class), any(PatientProfileUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("환자 프로필을 성공적으로 삭제한다.")
    @WithMockUser(username = "lhs")
    void successDeletePatientProfile() throws Exception {
        // given
        willDoNothing().given(patientService).deletePatientProfile(any(String.class));

        // when & then
        mockMvc.perform(delete("/api/v1/patient"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("환자 프로필을 등록할 때 잘못된 요청이면 예외가 발생한다.")
    @WithMockUser(username = "lhs")
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

    @Test
    @DisplayName("환자 프로필을 조회할 때 프로필이 없으면 예외가 발생한다.")
    @WithMockUser(username = "lhs")
    void failGetPatientProfileNotFound() throws Exception {
        // given
        given(patientService.getProfile(any(String.class))).willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));

        // when & then
        mockMvc.perform(get("/api/v1/patient"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("환자 프로필을 수정할 때 프로필이 없으면 예외가 발생한다.")
    @WithMockUser(username = "lhs")
    void failUpdatePatientProfileNotFound() throws Exception {
        // given
        PatientProfileUpdateRequest request = setUpPatientProfileUpdateRequest();
        willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST)).given(patientService).updatePatientProfile(any(String.class), any(PatientProfileUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("환자 프로필을 삭제할 때 프로필이 없으면 예외가 발생한다.")
    @WithMockUser(username = "lhs")
    void failDeletePatientProfileNotFound() throws Exception {
        // given
        willThrow(new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST)).given(patientService).deletePatientProfile(any(String.class));

        // when & then
        mockMvc.perform(delete("/api/v1/patient"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
