package com.patientpal.backend.caregiver.service;

import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.UPDATE_CAREGIVER_SIGNIFICANT;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.UPDATE_EXPERIENCE_YEARS;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.createCaregiverProfileRequest;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.huseongCaregiver;
import static com.patientpal.backend.fixtures.caregiver.CaregiverFixture.updateCaregiverProfileRequest;
import static com.patientpal.backend.fixtures.member.MemberFixture.huseongRoleCaregiver;
import static com.patientpal.backend.fixtures.member.MemberFixture.huseongRolePatient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
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
class CaregiverServiceTest {

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CaregiverService caregiverService;

    @Nested
    class 간병인_프로필_생성시에 {

        @Test
        void 성공한다() {
            // given
            Member member = huseongRoleCaregiver();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));
            when(caregiverRepository.save(any(Caregiver.class))).thenAnswer(invocation -> invocation.getArgument(0));
            CaregiverProfileCreateRequest request = createCaregiverProfileRequest();

            // when
            CaregiverProfileResponse response = caregiverService.saveCaregiverProfile(member.getUsername(), request, any(String.class));

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(member.getId());
            assertThat(response.getName()).isEqualTo(request.getName());
        }

        @Test
        void 권한이_없으면_예외가_발생한다() {
            // given
            Member member = huseongRolePatient();
            CaregiverProfileCreateRequest request = createCaregiverProfileRequest();
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> caregiverService.saveCaregiverProfile(member.getUsername(), request, any(String.class)))
                    .isInstanceOf(AuthorizationException.class);
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

        Caregiver caregiver;

        @BeforeEach
        void setUp() {
            caregiver = huseongCaregiver();
            when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(
                    Optional.of(caregiver.getMember()));
        }

        @Test
        void 조회를_성공한다() {
            // given
            when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));

            // when
            CaregiverProfileResponse response = caregiverService.getProfile(caregiver.getMember().getUsername());

            // then
            assertNotNull(response);
            assertThat(response.getMemberId()).isEqualTo(caregiver.getMember().getId());
            assertThat(response.getName()).isEqualTo(caregiver.getName());
            assertThat(response.getSpecialization()).isEqualTo(caregiver.getSpecialization());
        }

        @Test
        void 조회할_때_프로필이_없으면_예외가_발생한다() {
            // given
            when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> caregiverService.getProfile(caregiver.getMember().getUsername()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 수정을_성공한다() {
            // given
            when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));
            CaregiverProfileUpdateRequest request = updateCaregiverProfileRequest();

            // when
            caregiverService.updateCaregiverProfile(caregiver.getMember().getUsername(), request, any(String.class));

            // then
            assertThat(caregiver.getCaregiverSignificant()).isEqualTo(UPDATE_CAREGIVER_SIGNIFICANT);
            assertThat(caregiver.getExperienceYears()).isEqualTo(UPDATE_EXPERIENCE_YEARS);
        }

        @Test
        void 수정할_때_프로필이_없으면_예외가_발생한다() {
            // given
            when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.empty());
            CaregiverProfileUpdateRequest request = updateCaregiverProfileRequest();

            // when, then
            assertThatThrownBy(
                    () -> caregiverService.updateCaregiverProfile(caregiver.getMember().getUsername(), request, any(String.class)))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 삭제를_성공한다() {
            // given
            when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));

            // when
            caregiverService.deleteCaregiverProfile(caregiver.getMember().getUsername());

            // then
            verify(caregiverRepository).delete(caregiver);
            Optional<Caregiver> deletedCaregiver = caregiverRepository.findById(caregiver.getId());
            assertThat(deletedCaregiver).isEmpty();
        }

        // TODO 진행중 매칭 있을 시 삭제 불가능 구현 후 수정
        // @Test
        // void 삭제할_때_진행_중인_매칭이_있으면_예외가_발생한다() {
        //     // given
        //     when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));
        //     // 진행 중인 매칭 존재 시 예외 발생하도록 설정
        //     doThrow(new IllegalStateException("진행 중인 매칭이 있어 프로필을 삭제할 수 없습니다."))
        //             .when(caregiverRepository).delete(any(Caregiver.class));
        //
        //     // when, then
        //     assertThatThrownBy(() -> caregiverService.deleteCaregiverProfile(caregiver.getMember().getUsername()))
        //             .isInstanceOf(IllegalStateException.class)
        //             .hasMessage("진행 중인 매칭이 있어 프로필을 삭제할 수 없습니다.");
        // }
    }
}
