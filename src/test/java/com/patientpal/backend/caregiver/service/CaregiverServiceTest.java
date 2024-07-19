package com.patientpal.backend.caregiver.service;

import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.UPDATE_CAREGIVER_SIGNIFICANT;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.UPDATE_EXPERIENCE_YEARS;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.createCaregiverProfileRequest;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.updateCaregiverProfileRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileDetailResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.fixtures.caregiver.CaregiverFixture;
import com.patientpal.backend.fixtures.patient.PatientFixture;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
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
class CaregiverServiceTest {

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private ViewService viewService;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CaregiverService caregiverService;

    Caregiver caregiver = Mockito.spy(CaregiverFixture.defaultCaregiver());

    @Nested
    class 간병인_프로필_생성시에 {

        @Test
        void 성공한다() {
            // given
            Member member = CaregiverFixture.defaultCaregiver();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));
            when(caregiverRepository.findById(member.getId())).thenReturn(Optional.of(caregiver));
            CaregiverProfileCreateRequest request = createCaregiverProfileRequest();

            // when
            CaregiverProfileDetailResponse response = caregiverService.saveCaregiverProfile(member.getUsername(), request, any(String.class));

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(caregiver.getId());
            assertThat(response.getName()).isEqualTo(request.getName());
        }

        @Test
        void 권한이_없으면_예외가_발생한다() {
            // given
            CaregiverProfileCreateRequest request = createCaregiverProfileRequest();
            Member member = PatientFixture.defaultPatient();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> caregiverService.saveCaregiverProfile(member.getUsername(), request, any(String.class)))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        // TODO
        // @Test
        // void 중복_가입이면_예외가_발생한다() {
        //     // given
        //     Member member = huseongRoleCaregiver();
        //     when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));
        //     when(caregiverRepository.findByMember(member)).thenReturn(Optional.of(caregiver));
        //     CaregiverProfileCreateRequest request = createCaregiverProfileRequest();
        //
        //     // when, then
        //     assertThatThrownBy(() -> caregiverService.saveCaregiverProfile(member.getUsername(), request))
        //             .isInstanceOf(EntityNotFoundException.class);
        // }
    }

    @Nested
    class 간병인_프로필_조회_수정_삭제시에 {

        @BeforeEach
        void setUp() {
            when(caregiver.getId()).thenReturn(1L);
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(
                    Optional.of(caregiver));
        }

        @Test
        void 조회를_성공한다() {
            // given
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));

            CaregiverProfileDetailResponse response = caregiverService.getProfile(caregiver.getUsername(), caregiver.getId());

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(caregiver.getId());
            assertThat(response.getName()).isEqualTo(caregiver.getName());
            assertThat(response.getSpecialization()).isEqualTo(caregiver.getSpecialization());
        }

        @Test
        void 조회할_때_프로필이_없으면_예외가_발생한다() {
            // given
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> caregiverService.getProfile(caregiver.getUsername(), caregiver.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 수정을_성공한다() {
            // given
            CaregiverProfileUpdateRequest request = updateCaregiverProfileRequest();

            // when
            caregiverService.updateCaregiverProfile(caregiver.getUsername(), caregiver.getId(), request, any(String.class));

            // then
            assertThat(caregiver.getCaregiverSignificant()).isEqualTo(UPDATE_CAREGIVER_SIGNIFICANT);
            assertThat(caregiver.getExperienceYears()).isEqualTo(UPDATE_EXPERIENCE_YEARS);
        }

        @Test
        void 수정할_때_프로필이_없으면_예외가_발생한다() {
            // given
            CaregiverProfileUpdateRequest request = updateCaregiverProfileRequest();
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(
                    () -> caregiverService.updateCaregiverProfile(caregiver.getUsername(), caregiver.getId(), request, any(String.class)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class 간병인_프로필_매칭_리스트에_등록_시에 {

        @Test
        void 성공한다() {
            // given
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
            caregiver.setIsCompleteProfile(true);

            // when
            caregiverService.registerCaregiverProfileToMatchList(caregiver.getUsername(), caregiver.getId());

            // then
            assertThat(caregiver.getIsProfilePublic()).isTrue();
        }

        @Test
        void 프로필_미완성_시_예외가_발생한다() {
            // given
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
            caregiver.setIsCompleteProfile(false);

            // when & then
            assertThatThrownBy(() -> caregiverService.registerCaregiverProfileToMatchList(caregiver.getUsername(), caregiver.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PROFILE_NOT_COMPLETED.getMessage());
        }

        @Test
        void 권한이_없으면_예외가_발생한다() {
            // given
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));

            // when & then
            assertThatThrownBy(() -> caregiverService.registerCaregiverProfileToMatchList("wrongUsername", caregiver.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.AUTHORIZATION_FAILED.getMessage());
        }
    }

    @Nested
    class 간병인_프로필_매칭_리스트에_제거_시에 {

        @Test
        void 성공한다() {
            // given
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
            caregiver.setIsProfilePublic(true);

            // when
            caregiverService.unregisterCaregiverProfileToMatchList(caregiver.getUsername(), caregiver.getId());

            // then
            assertThat(caregiver.getIsProfilePublic()).isFalse();
        }

        @Test
        void 권한이_없으면_예외가_발생한다() {
            // given
            when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));

            // when & then
            assertThatThrownBy(() -> caregiverService.unregisterCaregiverProfileToMatchList("wrongUsername", caregiver.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.AUTHORIZATION_FAILED.getMessage());
        }
    }
}
