package com.patientpal.backend.matching.service;

import static com.patientpal.backend.fixtures.match.MatchFixture.createMatchForCaregiver;
import static com.patientpal.backend.fixtures.match.MatchFixture.defaultCaregiverForMatch;
import static com.patientpal.backend.fixtures.match.MatchFixture.defaultPatientForMatch;
import static com.patientpal.backend.fixtures.patient.PatientFixture.defaultPatient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.fixtures.caregiver.CaregiverFixture;
import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.matching.dto.request.CreateMatchCaregiverRequest;
import com.patientpal.backend.matching.dto.request.CreateMatchPatientRequest;
import com.patientpal.backend.matching.dto.response.ReceivedMatchListResponse;
import com.patientpal.backend.matching.dto.response.RequestMatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.matching.exception.CanNotRequestException;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.repository.PatientRepository;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
public class CaregiverMatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    Caregiver caregiver = Mockito.spy(defaultCaregiverForMatch());
    Patient patient = Mockito.spy(defaultPatientForMatch());
    Match match = createMatchForCaregiver(caregiver, patient);

    @Nested
    class 매칭_생성_시_간병인 {

        @Test
        void 성공한다() {
            // when
            when(caregiverRepository.findByUsername(caregiver.getUsername())).thenReturn(Optional.of(caregiver));
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
            CreateMatchCaregiverRequest createMatchCaregiverRequest = CreateMatchCaregiverRequest.builder().build();
            caregiver.setIsProfilePublic(true);
            patient.setIsProfilePublic(true);
            MatchResponse response = matchService.createMatchCaregiver(caregiver.getUsername(), patient.getId(), createMatchCaregiverRequest);

            // then
            assertThat(response.getFirstRequest()).isEqualTo(FirstRequest.CAREGIVER_FIRST);
            assertThat(response.getMatchStatus()).isEqualTo(MatchStatus.PENDING);
            assertNotNull(response);
            verify(matchRepository).save(any(Match.class));
        }

        @Test
        void 실패한다_상대_환자_매칭_검색_리스트에_미등록() {
            // when
            when(caregiverRepository.findByUsername(caregiver.getUsername())).thenReturn(Optional.of(caregiver));
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            caregiver.setIsProfilePublic(true);
            patient.setIsProfilePublic(false);

            // then
            assertThatThrownBy(() -> matchService.createMatchCaregiver(caregiver.getUsername(),
                    patient.getId(), any(CreateMatchCaregiverRequest.class))).isInstanceOf(CanNotRequestException.class);
        }

        @Test
        void 실패한다_이미_진행중인_매칭_존재() {
            // when
            when(caregiverRepository.findByUsername(caregiver.getUsername())).thenReturn(Optional.of(caregiver));
            when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            caregiver.setIsProfilePublic(true);
            patient.setIsProfilePublic(true);
            when(matchRepository.existsPendingMatch(caregiver.getId(), patient.getId())).thenReturn(true);


            // then
            assertThatThrownBy(() -> matchService.createMatchCaregiver(caregiver.getUsername(),
                    patient.getId(), any(CreateMatchCaregiverRequest.class))).isInstanceOf(DuplicateRequestException.class);
        }
    }

    @Nested
    class 간병인_매칭_단일_조회_시 {

        @Test
        void 성공한다() {
            // given
            when(memberRepository.findByUsername(any(String.class))).thenReturn(
                    Optional.of(match.getRequestMember()));
            when(matchRepository.findById(any())).thenReturn(Optional.of(match));

            // when
            MatchResponse response = matchService.getMatch(match.getId(), caregiver.getUsername());

            // then
            assertNotNull(response);
            assertThat(response.getRequestMemberName()).isEqualTo(match.getRequestMember().getName());
            assertThat(response.getReceivedMemberName()).isEqualTo(match.getReceivedMember().getName());
        }

        @Test
        void 실패한다_매칭_정보_없음() {
            // when
            when(matchRepository.findById(any(Long.class))).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> matchService.getMatch(1L, caregiver.getUsername()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 실패한다_내가_포함된_매칭_아님() {
            // given
            Match match = Match.builder()
                    .requestMember(defaultPatient())
                    .receivedMember(CaregiverFixture.defaultCaregiver())
                    .matchStatus(MatchStatus.PENDING)
                    .readStatus(ReadStatus.UNREAD)
                    .build();

            // when
            when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.of(caregiver));
            when(matchRepository.findById(any())).thenReturn(Optional.of(match));

            // then
            assertThatThrownBy(() -> matchService.getMatch(match.getId(), caregiver.getUsername()))
                    .isInstanceOf(AuthorizationException.class);
        }
    }

    @Nested
    class 간병인_요청보낸_매칭_리스트_조회_시 {

        @Test
        void 성공한다() {
            when(caregiver.getId()).thenReturn(1L);
            when(memberRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
            when(matchRepository.findAllRequest(any(), any())).thenReturn(new PageImpl<>(List.of(match)));

            RequestMatchListResponse response = matchService.getRequestMatches(caregiver.getUsername(), caregiver.getId(),
                    PageRequest.of(0, 10));

            assertNotNull(response);
            assertThat(response.getMatchList()).hasSize(1);
        }

        @Test
        void 실패한다_회원_정보_없음() {
            when(memberRepository.findById(caregiver.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> matchService.getRequestMatches(caregiver.getUsername(), caregiver.getId(), PageRequest.of(0, 10)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class 간병인_요청받은_매칭_조회_시 {

        @Test
        void 성공한다() {
            when(caregiver.getId()).thenReturn(1L);
            when(memberRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
            when(matchRepository.findAllReceived(any(), any())).thenReturn(new PageImpl<>(List.of(match)));

            ReceivedMatchListResponse response = matchService.getReceivedMatches(caregiver.getUsername(), caregiver.getId(),
                    PageRequest.of(0, 10));

            assertNotNull(response);
            assertThat(response.getMatchList()).hasSize(1);
        }

        @Test
        void 실패한다_회원_정보_없음() {
            when(memberRepository.findById(caregiver.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> matchService.getReceivedMatches(
                            caregiver.getUsername(), caregiver.getId(),  PageRequest.of(0, 10)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    /**
     * TODO
     * 매칭 수락, 취소 리팩토링 진행 후 테스트 작성 예정
     */
    @Nested
    class 간병인_매칭_수락_시 {


    }

    @Nested
    class 간병인_매칭_취소_시 {


    }
}
