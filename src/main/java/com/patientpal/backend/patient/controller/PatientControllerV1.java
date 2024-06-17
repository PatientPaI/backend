package com.patientpal.backend.patient.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.patientpal.backend.image.dto.ImageNameDto;
import com.patientpal.backend.image.service.PresignedUrlService;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "환자", description = "환자 프로필 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/patient")
public class PatientControllerV1 {

    private final PatientService patientService;
    private final PresignedUrlService presignedUrlService;

    @Operation(summary = "환자 프로필 생성", description = "환자 프로필을 새로 생성합니다. 선택적으로 이미지를 업로드할 수 있습니다.")
    @ApiResponse(responseCode = "201", description = "환자 프로필 생성 성공", content = @Content(schema = @Schema(implementation = PatientProfileResponse.class)))
    @PostMapping
    public ResponseEntity<PatientProfileResponse> createPatientProfile(
            @AuthenticationPrincipal User currentMember,
            @RequestBody @Valid PatientProfileCreateRequest patientProfileCreateRequest,
            @RequestParam(required = false) String profileImageUrl) {
        PatientProfileResponse patientProfileResponse = patientService.savePatientProfile(currentMember.getUsername(), patientProfileCreateRequest, presignedUrlService.getSavedUrl(profileImageUrl));
        return ResponseEntity.status(CREATED).body(patientProfileResponse);
    }

    @Operation(summary = "AWS S3에 저장될 전체 URL 경로 생성", description = "이미지 업로드를 위한 URL을 생성합니다. 생성된 URL로 파일 첨부 후 PUTMAPPING 진행 시 이미지가 저장됩니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공")
    @PostMapping("/presigned")
    public String createPresigned(@RequestBody ImageNameDto imageNameDto) {
        String imageName = imageNameDto.getImageName();
        return presignedUrlService.getPresignedUrl("profiles", imageName);
    }

    @Operation(summary = "환자 프로필 조회", description = "현재 로그인된 사용자의 환자 프로필을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "환자 프로필 조회 성공", content = @Content(schema = @Schema(implementation = PatientProfileResponse.class)))
    @GetMapping
    public ResponseEntity<PatientProfileResponse> getPatientProfile(
            @AuthenticationPrincipal User currentMember) {
        return ResponseEntity.status(OK).body(patientService.getProfile(currentMember.getUsername()));
    }

    @Operation(summary = "환자 프로필 수정", description = "환자 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 수정 성공")
    @PatchMapping
    public ResponseEntity<Void> updatePatientProfile(
            @AuthenticationPrincipal User currentMember,
            @RequestBody @Valid PatientProfileUpdateRequest patientProfileUpdateRequest,
            @RequestParam(required = false) String profileImageUrl) {
        patientService.updatePatientProfile(currentMember.getUsername(), patientProfileUpdateRequest, presignedUrlService.getSavedUrl(profileImageUrl));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 프로필 삭제", description = "환자 프로필을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 삭제 성공")
    @DeleteMapping
    public ResponseEntity<Void> deletePatientProfile(
            @AuthenticationPrincipal User currentMember) {
        patientService.deletePatientProfile(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 프로필 이미지 삭제", description = "환자 프로필에서 이미지를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 이미지 삭제 성공")
    @DeleteMapping("/image")
    public ResponseEntity<Void> deletePatientProfileImage(
            @AuthenticationPrincipal User currentMember) {
        patientService.deletePatientProfileImage(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 프로필 매칭 리스트에 등록", description = "환자 프로필을 매칭 리스트에 등록합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 매칭 리스트 등록 성공")
    @PostMapping("register/toMatchList")
    public ResponseEntity<Void> registerPatientProfileToMatchList(
            @AuthenticationPrincipal User currentMember) {
        patientService.registerPatientProfileToMatchList(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 프로필 매칭 리스트에서 제거", description = "환자 프로필을 매칭 리스트에서 제거합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 매칭 리스트 제거 성공")
    @PostMapping("unregister/toMatchList")
    public ResponseEntity<Void> unregisterPatientProfileToMatchList(
            @AuthenticationPrincipal User currentMember) {
        patientService.unregisterPatientProfileToMatchList(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }
}
