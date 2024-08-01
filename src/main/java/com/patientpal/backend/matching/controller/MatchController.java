package com.patientpal.backend.matching.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.lowagie.text.DocumentException;
import com.patientpal.backend.common.exception.ErrorResponse;
import com.patientpal.backend.matching.dto.request.CreateMatchCaregiverRequest;
import com.patientpal.backend.matching.dto.request.CreateMatchPatientRequest;
import com.patientpal.backend.matching.dto.response.CreateMatchResponse;
import com.patientpal.backend.matching.dto.response.ReceivedMatchListResponse;
import com.patientpal.backend.matching.dto.response.RequestMatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.matching.service.MatchService;
import com.patientpal.backend.matching.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MATCH API", description = "매칭 관련 API")
@RestController
@RequestMapping("api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final PdfService pdfService;

    @Operation(
            summary = "계약서 생성위한 요청 정보 조회",
            description = "로그인된 사용자가 계약서 생성할 때 필요한 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "계약서 전송 위한 기본 정보 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateMatchResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<CreateMatchResponse> create(@AuthenticationPrincipal User currentMember,
                                                      @RequestParam Long responseMemberId) {
        return ResponseEntity.status(OK).body(matchService.getCreateMatchRequest(currentMember.getUsername(), responseMemberId));
    }

    @Operation(
            summary = "계약서 전송 - 환자",
            description = "환자가 계약서를 작성하여 간병인에게 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "계약서 전송 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/patient")
    public ResponseEntity<MatchResponse> createMatchForPatient(@AuthenticationPrincipal User currentMember,
                                                               @RequestParam Long responseMemberId,
                                                               @RequestBody CreateMatchPatientRequest createMatchRequest) {
        return ResponseEntity.status(CREATED).body(matchService.createMatchPatient(currentMember.getUsername(), responseMemberId, createMatchRequest));
    }

    @Operation(
            summary = "계약서 전송 - 간병인",
            description = "간병인이 계약서를 작성하여 환자에게 전송합니다",
            responses = {
                    @ApiResponse(responseCode = "201", description = "계약서 전송 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/caregiver")
    public ResponseEntity<MatchResponse> createMatchForCaregiver(@AuthenticationPrincipal User currentMember,
                                                               @RequestParam Long responseMemberId,
                                                               @RequestBody CreateMatchCaregiverRequest createMatchRequest) {
        return ResponseEntity.status(CREATED).body(matchService.createMatchCaregiver(currentMember.getUsername(), responseMemberId, createMatchRequest));
    }

    @Operation(
            summary = "계약서 정보 단일 조회",
            description = "계약 ID로 계약서 정보를 조회합니다.",
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
    public ResponseEntity<MatchResponse> getMatch(@AuthenticationPrincipal User currentMember,
                                                  @PathVariable Long matchId) {
        final MatchResponse match = matchService.getMatch(matchId, currentMember.getUsername());
        return ResponseEntity.status(OK).body(match);
    }

    @Operation(
            summary = "요청 보낸 계약 리스트 요약 조회",
            description = "로그인된 사용자가 요청 보낸 요약된 계약 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "매칭 리스트 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RequestMatchListResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("{memberId}/all/request")
    public ResponseEntity<RequestMatchListResponse> getRequestMatchList(@AuthenticationPrincipal User currentMember,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @PathVariable Long memberId) {
        final RequestMatchListResponse matchList = matchService.getRequestMatches(currentMember.getUsername(), memberId,
                PageRequest.of(page, size));
        return ResponseEntity.status(OK).body(matchList);
    }

    @Operation(
            summary = "요청 받은 계약 리스트 요약 조회",
            description = "로그인된 사용자가 요청 받은 요약된 계약 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "매칭 리스트 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedMatchListResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("{memberId}/all/received")
    public ResponseEntity<ReceivedMatchListResponse> getReceivedMatchList(@AuthenticationPrincipal User currentMember,
                                                                          @PathVariable Long memberId,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        final ReceivedMatchListResponse matchList = matchService.getReceivedMatches(currentMember.getUsername(), memberId,
                PageRequest.of(page, size));
        return ResponseEntity.status(OK).body(matchList);
    }

    @Operation(
            summary = "보낸 계약서 신청 취소",
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
            summary = "받은 계약서 수락",
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

    @Operation(
            summary = "받은 계약서 PDF 다운로드",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF 다운로드 성공",
                            content = @Content(mediaType = "application/pdf")),
                    @ApiResponse(responseCode = "404", description = "매칭 정보 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/{matchId}/pdf")
    public ResponseEntity<InputStreamResource> downloadMatchPdf(@AuthenticationPrincipal User currentMember,
                                                                @PathVariable Long matchId) throws DocumentException {
        MatchResponse matchResponse = matchService.getMatch(matchId, currentMember.getUsername());
        ByteArrayInputStream bis = pdfService.generateMatchPdf(matchResponse);

        HttpHeaders headers = new HttpHeaders();
        //TODO 밑 inline 헤더가 제대로 동작하는지 테스트 해봐야 함.
        headers.add("Content-Disposition", "attachment; filename=" + matchResponse.getRequestMemberName() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
