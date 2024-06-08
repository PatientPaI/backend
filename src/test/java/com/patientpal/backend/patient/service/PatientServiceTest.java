package com.patientpal.backend.patient.service;

import static com.patientpal.backend.common.setup.CaregiverSetUpCommon.setUpCaregiver;
import static com.patientpal.backend.common.setup.PatientSetUpCommon.setUpPatient;
import static com.patientpal.backend.common.setup.PatientSetUpCommon.setUpPatientProfileCreateRequest;
import static com.patientpal.backend.common.setup.PatientSetUpCommon.setUpPatientProfileUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("환자 프로필을 성공적으로 생성한다.")
    void successSavePatientProfile() {
        // given
        Patient patient = setUpPatient();
        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PatientProfileCreateRequest request = setUpPatientProfileCreateRequest();

        // when
        PatientProfileResponse response = patientService.savePatientProfile(patient.getMember().getUsername(), request);

        // then
        assertNotNull(response);
        assertThat(response.getMemberId()).isEqualTo(patient.getMember().getId());
        assertThat(response.getName()).isEqualTo("patientlhs");
    }

    @Test
    @DisplayName("환자 프로필을 생성할 때 권한이 없으면 예외가 발생한다.")
    void failSavePatientProfileAuthorization() {
        // given
        Caregiver caregiver = setUpCaregiver();
        PatientProfileCreateRequest request = setUpPatientProfileCreateRequest();
        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));

        // when & then
        assertThatThrownBy(() -> patientService.savePatientProfile(caregiver.getMember().getUsername(), request))
                .isInstanceOf(AuthorizationException.class);
    }

    //TODO 중복 가입 구현 후 테스트 완성
//    @Test
//    @DisplayName("환자 프로필을 생성할 때 중복 가입이면 예외가 발생한다.")
//    void failSavePatientProfileDuplicate() {
//        // given
//        Patient patient = setUpPatient();
//        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
//        when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
//        PatientProfileCreateRequest request = setUpPatientProfileCreateRequest();
//
//        // when & then
//        assertThatThrownBy(() -> patientService.savePatientProfile(patient.getMember().getUsername(), request))
//                .isInstanceOf(EntityNotFoundException.class);
//    }

    @Test
    @DisplayName("환자 프로필을 성공적으로 조회한다.")
    void successGetProfile() {
        // given
        Patient patient = setUpPatient();
        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
        when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));

        // when
        PatientProfileResponse response = patientService.getProfile(patient.getMember().getUsername());

        // then
        assertNotNull(response);
        assertThat(response.getMemberId()).isEqualTo(patient.getMember().getId());
        assertThat(response.getName()).isEqualTo("sickLHS");
        assertThat(response.getNokContact()).isEqualTo("lhsChild");
    }

    @Test
    @DisplayName("환자 프로필을 조회할 때 프로필이 없으면 예외가 발생한다.")
    void failGetProfile() {
        // given
        Patient patient = setUpPatient();
        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
        when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> patientService.getProfile(patient.getMember().getUsername()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("환자 프로필을 성공적으로 수정한다.")
    void successUpdatePatientProfile() {
        // given
        Patient patient = setUpPatient();
        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
        when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
        PatientProfileUpdateRequest request = setUpPatientProfileUpdateRequest();

        // when
        patientService.updatePatientProfile(patient.getMember().getUsername(), request);

        // then
        assertThat(patient.getPatientSignificant()).isEqualTo("몸이 더 아파짐");
        assertThat(patient.getNokContact()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("환자 프로필을 수정할 때 프로필이 없으면 예외가 발생한다.")
    void failUpdatePatientProfile() {
        // given
        Patient patient = setUpPatient();
        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
        when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.empty());
        PatientProfileUpdateRequest request = setUpPatientProfileUpdateRequest();

        // when & then
        assertThatThrownBy(() -> patientService.updatePatientProfile(patient.getMember().getUsername(), request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("환자 프로필을 성공적으로 삭제한다.")
    void successDeletePatientProfile() {
        // given
        Patient patient = setUpPatient();
        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
        when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));

        // when
        patientService.deletePatientProfile(patient.getMember().getUsername());

        // then
        verify(patientRepository, times(1)).delete(patient);
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientRepository.findById(patient.getId()).orElseThrow(() -> new EntityNotFoundException(
                ErrorCode.PATIENT_NOT_EXIST))).isInstanceOf(EntityNotFoundException.class);
    }

    //TODO - PENDING이 하나라도 있을 시 삭제 불가능
//    @Test
//    @DisplayName("환자 프로필을 삭제할 때 진행 중인 매칭이 있으면 예외가 발생한다.")
//    void failDeletePatientProfile() {
//        // given
//        Patient patient = setUpPatient();
//        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
//        when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
//        // 진행 중인 매칭 존재 시 예외 발생하도록 설정
//        doThrow(new IllegalStateException("진행 중인 매칭이 있어 프로필을 삭제할 수 없습니다."))
//                .when(patientRepository).delete(any(Patient.class));
//
//        // when, then
//        assertThatThrownBy(() -> patientService.deletePatientProfile(patient.getMember().getUsername()))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessage("진행 중인 매칭이 있어 프로필을 삭제할 수 없습니다.");
//    }
}
