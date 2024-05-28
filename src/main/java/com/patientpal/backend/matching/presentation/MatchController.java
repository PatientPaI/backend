package com.patientpal.backend.matching.presentation;

import com.patientpal.backend.matching.application.MatchService;

import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.dto.response.CreateMatchResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public CreateMatchResponse createMatch(@AuthenticationPrincipal Member currentMember, @RequestParam Long responseMemberId) {
        Long matchId = matchService.create(currentMember.getId(), responseMemberId);
        return new CreateMatchResponse(matchId);
    }

    @GetMapping
    public List<MatchResponse> getMatchList(@AuthenticationPrincipal Member currentMember) {
        List<Match> matchList = matchService.findAllByUserId(currentMember.getId()); //추후 예외 처리
        List<MatchResponse> list = new ArrayList<>();
        for (Match m : matchList) {
            MatchResponse matchResponse = new MatchResponse(m.getId(), m.getPatient(), m.getCaregiver(), m.getCreatedDate(),
                    m.getMatchStatus(), m.getReadStatus(), m.getPatientProfileSnapshot(), m.getCaregiverProfileSnapshot());
            list.add(matchResponse);
        }
        return list;
    }

}
