package com.patientpal.backend.matching.presentation;

import com.patientpal.backend.matching.application.MatchService;

import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("api/v1/matches")
@RequiredArgsConstructor
@Slf4j
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(
            @RequestParam(name = "requestMemberId") Long requestMemberId,
            @RequestParam(name = "responseMemberId") Long responseMemberId) {
        final MatchResponse response = matchService.create(requestMemberId, responseMemberId);
        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<MatchResponse> getMatch(@RequestParam(name = "matchId") Long matchId) {
        MatchResponse match = matchService.getMatch(matchId);
        return ResponseEntity.status(OK).body(match);
    }

    /**
     * TODO
     * 페이징 처리 안됨
     * 2024-05-30T18:25:49.894+09:00  INFO 34732 --- [backend] [nio-8080-exec-1] c.p.b.m.application.MatchServiceImpl     : matchList.size=10
     * 2024-05-30T18:25:49.895+09:00  INFO 34732 --- [backend] [nio-8080-exec-1] c.p.b.m.application.MatchServiceImpl     : matchResponseList.size=1
     */
    @GetMapping("/all")
    public ResponseEntity<MatchListResponse> getMatchList(@AuthenticationPrincipal User currentMember,
                                                          @RequestParam(defaultValue = "0", name = "page") int page,
                                                          @RequestParam(defaultValue = "10", name = "size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        MatchListResponse matchList = matchService.getMatchList(currentMember.getUsername(), pageable);
        return ResponseEntity.status(OK).body(matchList);
    }

}
