package com.patientpal.backend.caregiver.service;

import static com.patientpal.backend.common.setup.CaregiverSetUpCommon.setUpCaregiver;
import static com.patientpal.backend.common.setup.CaregiverSetUpCommon.setUpCaregiverProfileCreateRequest;
import static com.patientpal.backend.common.setup.CaregiverSetUpCommon.setUpCaregiverProfileUpdateRequest;
import static com.patientpal.backend.common.setup.PatientSetUpCommon.setUpPatient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CaregiverServiceTest {

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CaregiverService caregiverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("간병인 프로필을 성공적으로 생성한다.")
    void successSaveCaregiverProfile() {
        // given
        Caregiver caregiver = setUpCaregiver();
        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));
        when(caregiverRepository.save(any(Caregiver.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CaregiverProfileCreateRequest request = setUpCaregiverProfileCreateRequest();

        // when
        CaregiverProfileResponse response = caregiverService.saveCaregiverProfile(caregiver.getMember().getUsername(), request);

        // then
        assertNotNull(response);
        assertThat(response.getMemberId()).isEqualTo(caregiver.getMember().getId());
        assertThat(response.getName()).isEqualTo("caregiverlhs");
    }

    @Test
    @DisplayName("간병인 프로필을 생성할 때 권한이 없으면 예외가 발생한다.")
    void failSaveCaregiverProfileAuthorization() {
        // given
        Patient patient = setUpPatient();
        CaregiverProfileCreateRequest request = setUpCaregiverProfileCreateRequest();
        when(memberRepository.findByUsername(patient.getMember().getUsername())).thenReturn(Optional.of(patient.getMember()));

        // when, then
        assertThatThrownBy(() -> caregiverService.saveCaregiverProfile(patient.getMember().getUsername(), request))
                .isInstanceOf(AuthorizationException.class);
    }

    //TODO
//    @Test
//    @DisplayName("간병인 프로필을 생성할 때 중복 가입이면 예외가 발생한다.")
//    void failSaveCaregiverProfileDuplicate() {
//        // given
//        Caregiver caregiver = setUpCaregiver();
//        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));
//        when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));
//        CaregiverProfileCreateRequest request = setUpCaregiverProfileCreateRequest();
//
//        // when, then
//        assertThatThrownBy(() -> caregiverService.saveCaregiverProfile(caregiver.getMember().getUsername(), request))
//                .isInstanceOf(EntityNotFoundException.class);
//    }

    @Test
    @DisplayName("간병인 프로필을 성공적으로 조회한다.")
    void successGetProfile() {
        // given
        Caregiver caregiver = setUpCaregiver();
        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));
        when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));

        // when
        CaregiverProfileResponse response = caregiverService.getProfile(caregiver.getMember().getUsername());

        // then
        assertNotNull(response);
        assertThat(response.getMemberId()).isEqualTo(caregiver.getMember().getId());
        assertThat(response.getName()).isEqualTo("sickLHS");
        assertThat(response.getSpecialization()).isEqualTo("전문 분야");
    }

    @Test
    @DisplayName("간병인 프로필을 조회할 때 프로필이 없으면 예외가 발생한다.")
    void failGetProfile() {
        // given
        Caregiver caregiver = setUpCaregiver();
        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));
        when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> caregiverService.getProfile(caregiver.getMember().getUsername()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("간병인 프로필을 성공적으로 수정한다.")
    void successUpdateCaregiverProfile() {
        // given
        Caregiver caregiver = setUpCaregiver();
        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));
        when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));
        CaregiverProfileUpdateRequest request = setUpCaregiverProfileUpdateRequest();

        // when
        caregiverService.updateCaregiverProfile(caregiver.getMember().getUsername(), request);

        // then
        assertThat(caregiver.getCaregiverSignificant()).isEqualTo("변경된 특이사항");
        assertThat(caregiver.getExperienceYears()).isEqualTo(10);
    }

    @Test
    @DisplayName("간병인 프로필을 수정할 때 프로필이 없으면 예외가 발생한다.")
    void failUpdateCaregiverProfile() {
        // given
        Caregiver caregiver = setUpCaregiver();
        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));
        when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.empty());
        CaregiverProfileUpdateRequest request = setUpCaregiverProfileUpdateRequest();

        // when, then
        assertThatThrownBy(() -> caregiverService.updateCaregiverProfile(caregiver.getMember().getUsername(), request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("간병인 프로필을 성공적으로 삭제한다.")
    void successDeleteCaregiverProfile() {
        // given
        Caregiver caregiver = setUpCaregiver();
        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));
        when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));

        // when
        caregiverService.deleteCaregiverProfile(caregiver.getMember().getUsername());

        // then
        verify(caregiverRepository, times(1)).delete(caregiver);
        when(caregiverRepository.findById(caregiver.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> caregiverRepository.findById(caregiver.getId()).orElseThrow(() -> new EntityNotFoundException(
                ErrorCode.CAREGIVER_NOT_EXIST))).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("간병인 프로필을 삭제할 때 진행 중인 매칭이 있으면 예외가 발생한다.")
    void failDeleteCaregiverProfile() {
        // given
        Caregiver caregiver = setUpCaregiver();
        when(memberRepository.findByUsername(caregiver.getMember().getUsername())).thenReturn(Optional.of(caregiver.getMember()));
        when(caregiverRepository.findByMember(caregiver.getMember())).thenReturn(Optional.of(caregiver));
        // 진행 중인 매칭 존재 시 예외 발생하도록 설정
        doThrow(new IllegalStateException("진행 중인 매칭이 있어 프로필을 삭제할 수 없습니다."))
                .when(caregiverRepository).delete(any(Caregiver.class));

        // when, then
        assertThatThrownBy(() -> caregiverService.deleteCaregiverProfile(caregiver.getMember().getUsername()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("진행 중인 매칭이 있어 프로필을 삭제할 수 없습니다.");
    }
}
