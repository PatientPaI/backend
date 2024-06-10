package com.patientpal.backend.patient.service;

import static com.patientpal.backend.fixtures.member.MemberFixture.huseongRoleCaregiver;
import static com.patientpal.backend.fixtures.member.MemberFixture.huseongRolePatient;
import static com.patientpal.backend.fixtures.patient.PatientFixture.UPDATE_NOK_CONTACT;
import static com.patientpal.backend.fixtures.patient.PatientFixture.UPDATE_PATIENT_SIGNIFICANT;
import static com.patientpal.backend.fixtures.patient.PatientFixture.createPatientProfileRequest;
import static com.patientpal.backend.fixtures.patient.PatientFixture.huseongPatient;
import static com.patientpal.backend.fixtures.patient.PatientFixture.updatePatientProfileRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PatientService patientService;

    @Nested
    class 간병인_프로필_생성 {

        @Test
        @DisplayName("환자 프로필을 성공적으로 생성한다.")
        void successSavePatientProfile() {
            // given
            Member member = huseongRolePatient();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
            PatientProfileCreateRequest request = createPatientProfileRequest();

            // when
            PatientProfileResponse response = patientService.savePatientProfile(member.getUsername(), request);

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(member.getId());
            assertThat(response.getName()).isEqualTo(request.getName());
        }

        @Test
        @DisplayName("환자 프로필을 생성할 때 권한이 없으면 예외가 발생한다.")
        void failSavePatientProfileAuthorization() {
            // given
            Member member = huseongRoleCaregiver();
            PatientProfileCreateRequest request = createPatientProfileRequest();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> patientService.savePatientProfile(member.getUsername(), request))
                    .isInstanceOf(AuthorizationException.class);
        }

        //TODO 중복 가입 구현 후 테스트 완성
//    @Test
//    @DisplayName("환자 프로필을 생성할 때 중복 가입이면 예외가 발생한다.")
//    void failSavePatientProfileDuplicate() {
//        // given
//        Patient patient = huseongRolePatient();
//        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
//        when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
//        PatientProfileCreateRequest request = createPatientProfileRequest();
//
//        // when & then
//        assertThatThrownBy(() -> patientService.savePatientProfile(patient.getMember().getUsername(), request))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
    }

    @Nested
    class 환자_프로필_조회_수정_삭제 {

        Patient patient;

        @BeforeEach
        void setUp() {
            patient = huseongPatient();
            when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(
                    Optional.of(patient.getMember()));
        }

        @Test
        @DisplayName("환자 프로필을 성공적으로 조회한다.")
        void successGetProfile() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));

            // when
            PatientProfileResponse response = patientService.getProfile(patient.getMember().getUsername());

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(patient.getMember().getId());
            assertThat(response.getNokContact()).isEqualTo(patient.getNokContact());
        }

        @Test
        @DisplayName("환자 프로필을 조회할 때 프로필이 없으면 예외가 발생한다.")
        void failGetProfile() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> patientService.getProfile(patient.getMember().getUsername()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("환자 프로필을 성공적으로 수정한다.")
        void successUpdatePatientProfile() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
            PatientProfileUpdateRequest request = updatePatientProfileRequest();

            // when
            patientService.updatePatientProfile(patient.getMember().getUsername(), request);

            // then
            assertThat(patient.getPatientSignificant()).isEqualTo(UPDATE_PATIENT_SIGNIFICANT);
            assertThat(patient.getNokContact()).isEqualTo(UPDATE_NOK_CONTACT);
        }

        @Test
        @DisplayName("환자 프로필을 수정할 때 프로필이 없으면 예외가 발생한다.")
        void failUpdatePatientProfile() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.empty());
            PatientProfileUpdateRequest request = updatePatientProfileRequest();

            // when & then
            assertThatThrownBy(() -> patientService.updatePatientProfile(patient.getMember().getUsername(), request))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("환자 프로필을 성공적으로 삭제한다.")
        void successDeletePatientProfile() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));

            // when
            patientService.deletePatientProfile(patient.getMember().getUsername());

            // then
            verify(patientRepository).delete(patient);
            Optional<Patient> deletedPatient = patientRepository.findById(patient.getId());
            assertThat(deletedPatient).isEmpty();
        }

        //TODO - PENDING이 하나라도 있을 시 삭제 불가능
//    @Test
//    @DisplayName("환자 프로필을 삭제할 때 진행 중인 매칭이 있으면 예외가 발생한다.")
//    void failDeletePatientProfile() {
//        // given
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
}
