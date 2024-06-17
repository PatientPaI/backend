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
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.exception.CanNotDeleteException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PatientService patientService;

    @Nested
    class 간병인_프로필_생성시에 {

        @Test
        void 성공적으로_생성한다() {
            // given
            Member member = huseongRolePatient();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
            PatientProfileCreateRequest request = createPatientProfileRequest();

            // when
            PatientProfileResponse response = patientService.savePatientProfile(member.getUsername(), request, any(String.class));

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(member.getId());
            assertThat(response.getName()).isEqualTo(request.getName());
        }

        @Test
        void 권한이_없으면_예외가_발생한다() {
            // given
            Member member = huseongRoleCaregiver();
            PatientProfileCreateRequest request = createPatientProfileRequest();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> patientService.savePatientProfile(member.getUsername(), request, any(String.class)))
                    .isInstanceOf(AuthorizationException.class);
        }

        // TODO 휴대폰 인증 진행 & 중복 가입 구현 후 테스트 완성
        // @Test
        // void 중복가입이면_예외가_발생한다() {
        //     // given
        //     Patient patient = huseongRolePatient();
        //     when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));
        //     when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
        //     PatientProfileCreateRequest request = createPatientProfileRequest();
        //
        //     // when & then
        //     assertThatThrownBy(() -> patientService.savePatientProfile(patient.getMember().getUsername(), request))
        //             .isInstanceOf(EntityNotFoundException.class);
        // }
    }

    @Nested
    class 환자_프로필_조회_수정_삭제시에 {

        Patient patient;

        @BeforeEach
        void setUp() {
            patient = huseongPatient();
            when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(
                    Optional.of(patient.getMember()));
        }

        @Test
        void 조회를_성공한다() {
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
        void 조회할_때_프로필이_없으면_예외가_발생한다() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> patientService.getProfile(patient.getMember().getUsername()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 수정을_성공한다() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
            PatientProfileUpdateRequest request = updatePatientProfileRequest();

            // when
            patientService.updatePatientProfile(patient.getMember().getUsername(), request, any(String.class));

            // then
            assertThat(patient.getPatientSignificant()).isEqualTo(UPDATE_PATIENT_SIGNIFICANT);
            assertThat(patient.getNokContact()).isEqualTo(UPDATE_NOK_CONTACT);
        }

        @Test
        void 수정할_때_프로필이_없으면_예외가_발생한다() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.empty());
            PatientProfileUpdateRequest request = updatePatientProfileRequest();

            // when & then
            assertThatThrownBy(() -> patientService.updatePatientProfile(patient.getMember().getUsername(), request, any(String.class)))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 삭제를_성공한다() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
            when(matchRepository.existsInProgressMatchingForPatient(patient.getId())).thenReturn(false);

            // when
            patientService.deletePatientProfile(patient.getMember().getUsername());

            // then
            verify(patientRepository).delete(patient);
            Optional<Patient> deletedPatient = patientRepository.findById(patient.getId());
            assertThat(deletedPatient).isEmpty();
        }

        @Test
        void 삭제할_때_진행중인_매칭이_있으면_예외가_발생한다() {
            // given
            when(patientRepository.findByMember(patient.getMember())).thenReturn(Optional.of(patient));
            when(matchRepository.existsInProgressMatchingForPatient(patient.getId())).thenReturn(true);

            // when, then
            assertThatThrownBy(() -> patientService.deletePatientProfile(patient.getMember().getUsername()))
                    .isInstanceOf(CanNotDeleteException.class);
        }
    }
}
