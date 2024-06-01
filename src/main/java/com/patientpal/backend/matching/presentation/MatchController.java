package com.patientpal.backend.matching.presentation;

import com.patientpal.backend.matching.application.MatchService;

import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * TODO
     *  - createMatch()시, createdBy 에 null 들어가는 문제 ->
     *  아니면 멤버를 통해 환자, 간병인으로 나뉘고 매치가 등록돼서 누구로 등록해야할지 모르나?
     *  - Matching 신청 할 때, 프로필을 등록했는지 확인하는 기능 필수. 현재 Patient, Caregiver 엔티티는 회원 가입 시, ID만 가진 빈껍데기로 생성이 됨.
     *  그 안에 세부 내용들을 채워넣어야 매칭 신청, 등록 가능. -> 프로필 세부 내용을 작성해야 매칭 신청 or 매칭 리스트에 등록 가능하게 해야함.
     */

    @PostMapping("/patient/{responseMemberId}")
    public ResponseEntity<MatchResponse> createMatchForPatient(@AuthenticationPrincipal User currentMember,
                                               @PathVariable Long responseMemberId) {
        return ResponseEntity.status(CREATED).body(matchService.createForPatient(currentMember, responseMemberId));
    }

    @PostMapping("/caregiver/{responseMemberId}")
    public ResponseEntity<MatchResponse> createMatchForCaregiver(@AuthenticationPrincipal User currentMember,
                                                 @PathVariable Long responseMemberId) {
        return ResponseEntity.status(CREATED).body(matchService.createForCaregiver(currentMember, responseMemberId));
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponse> getMatch(@AuthenticationPrincipal User currentMember, @PathVariable Long matchId) {
        final MatchResponse match = matchService.getMatch(matchId, currentMember.getUsername());
        return ResponseEntity.status(OK).body(match);
    }

    @GetMapping("/all")
    public ResponseEntity<MatchListResponse> getMatchList(@AuthenticationPrincipal User currentMember,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        final MatchListResponse matchList = matchService.getMatchList(currentMember.getUsername(), PageRequest.of(page, size));
        return ResponseEntity.status(OK).body(matchList);
    }

    @PostMapping("/{matchId}/cancel")
    public ResponseEntity<Void> cancelMatch(@PathVariable Long matchId, @AuthenticationPrincipal User currentMember) {
        matchService.cancelMatch(matchId, currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{matchId}/accept")
    public ResponseEntity<Void> acceptMatch(@PathVariable Long matchId, @AuthenticationPrincipal User currentMember) {
        matchService.acceptMatch(matchId, currentMember.getUsername());
        return ResponseEntity.ok().build();
    }
}
