package com.patientpal.backend.matching.service;

import static com.patientpal.backend.fixtures.match.MatchFixture.caregiverMemberForMatch;
import static com.patientpal.backend.fixtures.match.MatchFixture.createMatchForPatient;
import static com.patientpal.backend.fixtures.match.MatchFixture.patientMemberForMatch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.fixtures.caregiver.CaregiverFixture;
import com.patientpal.backend.fixtures.member.MemberFixture;
import com.patientpal.backend.fixtures.patient.PatientFixture;
import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.matching.exception.CanNotReadException;
import com.patientpal.backend.matching.exception.CanNotRequestException;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.matching.exception.NotCompleteProfileException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
public class PatientMatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    private final Member requestMember = patientMemberForMatch();
    private final Member responseMember = caregiverMemberForMatch();
    private final Match match = createMatchForPatient(requestMember, responseMember);

    @Nested
    class 매칭_생성_시_환자 {

        @Test
        void 성공한다() {
            // when
            when(memberRepository.findByUsername(requestMember.getUsername())).thenReturn(Optional.of(requestMember));
            when(memberRepository.findById(responseMember.getId())).thenReturn(Optional.of(responseMember));
            when(matchRepository.existsPendingMatchForPatient(any(), any())).thenReturn(false);
            when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
            MatchResponse response = matchService.createForPatient(requestMember.getUsername(), responseMember.getId());

            // then
            assertThat(response.getFirstRequest()).isEqualTo(FirstRequest.PATIENT_FIRST);
            assertThat(response.getMatchStatus()).isEqualTo(MatchStatus.PENDING);
            assertThat(response.getPatientProfileSnapshot()).isNotNull();
            assertNotNull(response);
            verify(matchRepository).save(any(Match.class));
        }

        @Test
        void 실패한다_내_프로필_미등록() {
            // given
            Member requestMember = MemberFixture.huseongRolePatient();
            Member responseMember = caregiverMemberForMatch();

            // when
            when(memberRepository.findByUsername(requestMember.getUsername())).thenReturn(Optional.of(requestMember));
            when(memberRepository.findById(responseMember.getId())).thenReturn(Optional.of(responseMember));

            // then
            assertThatThrownBy(() -> matchService.createForPatient(requestMember.getUsername(),
                    responseMember.getId())).isInstanceOf(NotCompleteProfileException.class);
        }

        @Test
        void 실패한다_상대_간병인_프로필_없음() {
            // given
            Member requestMember = patientMemberForMatch();
            Member responseMember = MemberFixture.huseongRoleCaregiver();

            // when
            when(memberRepository.findByUsername(requestMember.getUsername())).thenReturn(Optional.of(requestMember));
            when(memberRepository.findById(responseMember.getId())).thenReturn(Optional.of(responseMember));

            // then
            assertThatThrownBy(() -> matchService.createForPatient(requestMember.getUsername(),
                    responseMember.getId())).isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 실패한다_상대_간병인_매칭_검색_리스트에_미등록() {
            // when
            when(memberRepository.findByUsername(requestMember.getUsername())).thenReturn(Optional.of(requestMember));
            when(memberRepository.findById(responseMember.getId())).thenReturn(Optional.of(responseMember));
            responseMember.getCaregiver().setIsInMatchList(false);

            // then
            assertThatThrownBy(() -> matchService.createForPatient(requestMember.getUsername(),
                    responseMember.getId())).isInstanceOf(CanNotRequestException.class);
        }

        @Test
        void 실패한다_이미_진행중인_매칭_존재() {
            when(memberRepository.findByUsername(requestMember.getUsername())).thenReturn(Optional.of(requestMember));
            when(memberRepository.findById(responseMember.getId())).thenReturn(Optional.of(responseMember));
            // when
            when(matchRepository.existsPendingMatchForPatient(any(), any())).thenReturn(true);

            // then
            assertThatThrownBy(() -> matchService.createForPatient(requestMember.getUsername(),
                    responseMember.getId())).isInstanceOf(DuplicateRequestException.class);
        }
    }

    @Nested
    class 환자_매칭_단일_조회_시 {

        @Test
        void 성공한다() {
            // given
            when(memberRepository.findByUsername(any(String.class))).thenReturn(
                    Optional.of(match.getPatient().getMember()));
            when(matchRepository.findById(any())).thenReturn(Optional.of(match));

            // when
            MatchResponse response = matchService.getMatch(match.getId(), requestMember.getUsername());

            // then
            assertNotNull(response);
            assertThat(response.getPatientId()).isEqualTo(match.getPatient().getId());
            assertThat(response.getCaregiverId()).isEqualTo(match.getCaregiver().getId());
        }

        @Test
        void 실패한다_매칭_정보_없음() {
            // when
            when(matchRepository.findById(any(Long.class))).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> matchService.getMatch(1L, requestMember.getUsername()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void 실패한다_내가_포함된_매칭_아님() {
            // given
            Match match = Match.builder()
                    .patient(PatientFixture.huseongPatient())
                    .caregiver(CaregiverFixture.huseongCaregiver())
                    .matchStatus(MatchStatus.PENDING)
                    .readStatus(ReadStatus.UNREAD)
                    .build();

            // when
            when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.of(requestMember));
            when(matchRepository.findById(any())).thenReturn(Optional.of(match));

            // then
            assertThatThrownBy(() -> matchService.getMatch(match.getId(), requestMember.getUsername()))
                    .isInstanceOf(AuthorizationException.class);
        }

        @Test
        void 실패한다_이미_취소된_매칭() {
            // when
            when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.of(requestMember));
            when(matchRepository.findById(any())).thenReturn(Optional.of(match));
            match.setMatchStatus(MatchStatus.CANCELED);

            // then
            assertThatThrownBy(() -> matchService.getMatch(match.getId(), requestMember.getUsername()))
                    .isInstanceOf(CanNotReadException.class);
        }
    }

    @Nested
    class 환자_요청보낸_매칭_리스트_조회_시 {

        @Test
        void 성공한다() {
            when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.of(requestMember));
            when(matchRepository.findAllRequestByPatientId(any(), any())).thenReturn(new PageImpl<>(List.of(match)));

            MatchListResponse response = matchService.getRequestMatches(requestMember.getUsername(),
                    PageRequest.of(0, 10));

            assertNotNull(response);
            assertThat(response.getMatchList()).hasSize(1);
        }

        @Test
        void 실패한다_회원_정보_없음() {
            when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> matchService.getRequestMatches(requestMember.getUsername(), PageRequest.of(0, 10)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class 환자_요청받은_매칭_조회_시 {

        @Test
        void 성공한다() {
            when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.of(requestMember));
            when(matchRepository.findAllReceivedByPatientId(any(), any())).thenReturn(new PageImpl<>(List.of(match)));

            MatchListResponse response = matchService.getReceivedMatches(requestMember.getUsername(),
                    PageRequest.of(0, 10));

            assertNotNull(response);
            assertThat(response.getMatchList()).hasSize(1);
        }

        @Test
        void 실패한다_회원_정보_없음() {
            when(memberRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> matchService.getReceivedMatches(requestMember.getUsername(), PageRequest.of(0, 10)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    /**
     * TODO
     * 매칭 수락, 취소 리팩토링 진행 후 테스트 작성 예정
     */
    @Nested
    class 환자_매칭_수락_시 {

    }

    @Nested
    class 환자_매칭_취소_시 {


    }
}
