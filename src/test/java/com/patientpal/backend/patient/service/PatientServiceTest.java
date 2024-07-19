package com.patientpal.backend.patient.service;

import static com.patientpal.backend.fixtures.member.MemberFixture.defaultRoleCaregiver;
import static com.patientpal.backend.fixtures.member.MemberFixture.defaultRolePatient;
import static com.patientpal.backend.fixtures.patient.PatientFixture.UPDATE_NOK_CONTACT;
import static com.patientpal.backend.fixtures.patient.PatientFixture.UPDATE_PATIENT_SIGNIFICANT;
import static com.patientpal.backend.fixtures.patient.PatientFixture.createPatientProfileRequest;
import static com.patientpal.backend.fixtures.patient.PatientFixture.defaultPatient;
import static com.patientpal.backend.fixtures.patient.PatientFixture.updatePatientProfileRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileDetailResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import com.patientpal.backend.view.ViewService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ViewService viewService;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PatientService patientService;

    Patient patient = Mockito.spy(defaultPatient());

    @Nested
    class 환자_프로필_생성시에 {

        @Test
        void 성공적으로_생성한다() {
            // given
            Member member = defaultRolePatient();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));
            when(patientRepository.findById(member.getId())).thenReturn(Optional.of(patient));
            PatientProfileCreateRequest request = createPatientProfileRequest();

            // when
            PatientProfileDetailResponse response = patientService.savePatientProfile(member.getUsername(), request, any(String.class));

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(member.getId());
            assertThat(response.getName()).isEqualTo(request.getName());
        }

        @Test
        void 권한이_없으면_예외가_발생한다() {
            // given
            PatientProfileCreateRequest request = createPatientProfileRequest();
            Member member = defaultRoleCaregiver();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> patientService.savePatientProfile(member.getUsername(), request, any(String.class)))
                    .isInstanceOf(EntityNotFoundException.class);
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

        @BeforeEach
        void setUp() {
            when(patient.getId()).thenReturn(1L);
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        }

        @Test
        void 조회를_성공한다() {
            // given
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

            // when
            PatientProfileDetailResponse response = patientService.getProfile(patient.getUsername(), patient.getId());

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(patient.getId());
            assertThat(response.getNokContact()).isEqualTo(patient.getNokContact());
        }

        @Test
        void 조회할_때_프로필이_없으면_예외가_발생한다() {
            // given
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> patientService.getProfile(patient.getUsername(), patient.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 수정을_성공한다() {
            // given
            PatientProfileUpdateRequest request = updatePatientProfileRequest();

            // when
            patientService.updatePatientProfile(patient.getUsername(), patient.getId(), request, any(String.class));

            // then
            assertThat(patient.getPatientSignificant()).isEqualTo(UPDATE_PATIENT_SIGNIFICANT);
            assertThat(patient.getNokContact()).isEqualTo(UPDATE_NOK_CONTACT);
        }

        @Test
        void 수정할_때_프로필이_없으면_예외가_발생한다() {
            // given
            PatientProfileUpdateRequest request = updatePatientProfileRequest();
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> patientService.updatePatientProfile(patient.getUsername(), patient.getId(), request, any(String.class)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class 환자_프로필_매칭_리스트에_제거_시에 {

        @Test
        void 성공한다() {
            // given
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            patient.setIsProfilePublic(true);

            // when
            patientService.unregisterPatientProfileToMatchList(patient.getUsername(), patient.getId());

            // then
            assertThat(patient.getIsProfilePublic()).isFalse();
        }

        @Test
        void 권한이_없으면_예외가_발생한다() {
            // given
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

            // when & then
            assertThatThrownBy(() -> patientService.unregisterPatientProfileToMatchList("wrongUsername", patient.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.AUTHORIZATION_FAILED.getMessage());
        }
    }
}
