package com.patientpal.backend.matching.controller;

import com.patientpal.backend.common.exception.ErrorResponse;
import com.patientpal.backend.matching.service.MatchService;
import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "MATCH API", description = "매칭 관련 API")
@RestController
@RequestMapping("api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @Operation(
            summary = "매칭 생성 for 환자",
            responses = {
                    @ApiResponse(responseCode = "201", description = "매칭 생성 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/patient/{responseMemberId}")
    public ResponseEntity<MatchResponse> createMatchForPatient(@AuthenticationPrincipal User currentMember,
                                                               @PathVariable Long responseMemberId) {
        return ResponseEntity.status(CREATED).body(matchService.createForPatient(currentMember, responseMemberId));
    }

    @Operation(
            summary = "매칭 생성 for 간병인",
            responses = {
                    @ApiResponse(responseCode = "201", description = "매칭 생성 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/caregiver/{responseMemberId}")
    public ResponseEntity<MatchResponse> createMatchForCaregiver(@AuthenticationPrincipal User currentMember,
                                                                 @PathVariable Long responseMemberId) {
        return ResponseEntity.status(CREATED).body(matchService.createForCaregiver(currentMember, responseMemberId));
    }

    @Operation(
            summary = "매칭 정보 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "매칭 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponse.class))),
                    @ApiResponse(responseCode = "404", description = "매칭 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponse> getMatch(@AuthenticationPrincipal User currentMember, @PathVariable Long matchId) {
        final MatchResponse match = matchService.getMatch(matchId, currentMember.getUsername());
        return ResponseEntity.status(OK).body(match);
    }

    @Operation(
            summary = "매칭 리스트 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "매칭 리스트 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchListResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/all")
    public ResponseEntity<MatchListResponse> getMatchList(@AuthenticationPrincipal User currentMember,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        final MatchListResponse matchList = matchService.getMatchList(currentMember.getUsername(), PageRequest.of(page, size));
        return ResponseEntity.status(OK).body(matchList);
    }

    @Operation(
            summary = "매칭 취소",
            responses = {
                    @ApiResponse(responseCode = "204", description = "매칭 취소 성공"),
                    @ApiResponse(responseCode = "404", description = "매칭 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/{matchId}/cancel")
    public ResponseEntity<Void> cancelMatch(@PathVariable Long matchId, @AuthenticationPrincipal User currentMember) {
        matchService.cancelMatch(matchId, currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "매칭 수락",
            responses = {
                    @ApiResponse(responseCode = "200", description = "매칭 수락 성공"),
                    @ApiResponse(responseCode = "404", description = "매칭 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("{matchId}/accept")
    public ResponseEntity<Void> acceptMatch(@PathVariable Long matchId, @AuthenticationPrincipal User currentMember) {
        matchService.acceptMatch(matchId, currentMember.getUsername());
        return ResponseEntity.ok().build();
    }
}
