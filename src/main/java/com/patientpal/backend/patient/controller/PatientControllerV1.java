package com.patientpal.backend.patient.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileListResponse;
import com.patientpal.backend.common.TimeTrace;
import com.patientpal.backend.image.dto.ImageNameDto;
import com.patientpal.backend.image.service.PresignedUrlService;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileDetailResponse;
import com.patientpal.backend.common.utils.PageableUtil;
import com.patientpal.backend.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "환자", description = "환자 프로필 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1/patient")
public class PatientControllerV1 {

    private final PatientService patientService;
    private final PresignedUrlService presignedUrlService;

    @Operation(summary = "환자 프로필 생성", description = "환자 프로필을 새로 생성합니다. 선택적으로 이미지를 업로드할 수 있습니다.")
    @ApiResponse(responseCode = "201", description = "환자 프로필 생성 성공", content = @Content(schema = @Schema(implementation = PatientProfileDetailResponse.class)))
    @PostMapping("/profile")
    @TimeTrace
    public ResponseEntity<PatientProfileDetailResponse> createPatientProfile(
            @AuthenticationPrincipal User currentMember,
            @RequestBody @Valid PatientProfileCreateRequest patientProfileCreateRequest,
            @RequestParam(required = false) String profileImageUrl) {
        String savedCloudFrontUrl = presignedUrlService.getCloudFrontUrl("profiles", profileImageUrl);
        PatientProfileDetailResponse patientProfileDetailResponse = patientService.savePatientProfile(currentMember.getUsername(), patientProfileCreateRequest, savedCloudFrontUrl);
        return ResponseEntity.status(CREATED).body(patientProfileDetailResponse);
    }

    @Operation(summary = "AWS S3에 저장될 전체 URL 경로 생성", description = "이미지 업로드를 위한 URL을 생성합니다. 생성된 URL로 파일 첨부 후 PUTMAPPING 진행 시 이미지가 저장됩니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공")
    @PostMapping("/presigned")
    public String createPresigned(@RequestBody ImageNameDto imageNameDto) {
        String imageName = imageNameDto.getImageName();
        return presignedUrlService.getPresignedUrl("profiles", imageName);
    }

    @Operation(summary = "환자 프로필 조회", description = "현재 로그인된 사용자의 환자 프로필을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "환자 프로필 조회 성공", content = @Content(schema = @Schema(implementation = PatientProfileDetailResponse.class)))
    @GetMapping("/profile/{memberId}")
    public ResponseEntity<PatientProfileDetailResponse> getPatientProfile(@AuthenticationPrincipal User currentMember, @PathVariable Long memberId) {
        return ResponseEntity.status(OK).body(patientService.getProfile(currentMember.getUsername(), memberId));
    }

    @Operation(summary = "환자 프로필 수정", description = "환자 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 수정 성공")
    @PatchMapping("/profile/{memberId}")
    public ResponseEntity<Void> updatePatientProfile(
            @AuthenticationPrincipal User currentMember,
            @PathVariable Long memberId,
            @RequestBody @Valid PatientProfileUpdateRequest patientProfileUpdateRequest,
            @RequestParam(required = false) String profileImageUrl) {
        String savedCloudFrontUrl = presignedUrlService.getCloudFrontUrl("profiles", profileImageUrl);
        patientService.updatePatientProfile(currentMember.getUsername(), memberId, patientProfileUpdateRequest, savedCloudFrontUrl);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 프로필 삭제", description = "환자 프로필 정보를 삭제합니다. - 다른 환자로 변경 가능")
    @ApiResponse(responseCode = "204", description = "환자 프로필 삭제 성공")
    @DeleteMapping("/profile/{memberId}")
    public ResponseEntity<Void> deletePatientProfile(@AuthenticationPrincipal User currentMember, @PathVariable Long memberId) {
        patientService.deletePatientProfile(currentMember.getUsername(), memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 프로필 이미지 삭제", description = "환자 프로필에서 이미지를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 이미지 삭제 성공")
    @DeleteMapping("{memberId}/image")
    public ResponseEntity<Void> deletePatientProfileImage(@AuthenticationPrincipal User currentMember, @PathVariable Long memberId) {
        patientService.deletePatientProfileImage(currentMember.getUsername(), memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 프로필 매칭 리스트에 등록", description = "환자 프로필을 매칭 리스트에 등록합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 매칭 리스트 등록 성공")
    @PostMapping("/profile/{memberId}/register/toMatchList")
    public ResponseEntity<Void> registerPatientProfileToMatchList(@AuthenticationPrincipal User currentMember, @PathVariable Long memberId) {
        patientService.registerPatientProfileToMatchList(currentMember.getUsername(), memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 프로필 매칭 리스트에서 제거", description = "환자 프로필을 매칭 리스트에서 제거합니다.")
    @ApiResponse(responseCode = "204", description = "환자 프로필 매칭 리스트 제거 성공")
    @PostMapping("/profile/{memberId}/unregister/toMatchList")
    public ResponseEntity<Void> unregisterPatientProfileToMatchList(@AuthenticationPrincipal User currentMember, @PathVariable Long memberId) {
        patientService.unregisterPatientProfileToMatchList(currentMember.getUsername(), memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "간병인 찾기", description = "지역, 이름, 성별, 나이로 간병인을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "조건에 해당하는 간병인 찾기 성공")
    @GetMapping("/search")
    public ResponseEntity<CaregiverProfileListResponse> searchCaregivers(ProfileSearchCondition condition,
                                                                         @RequestParam(required = false) Long lastIndex,
                                                                         @RequestParam(required = false) LocalDateTime lastProfilePublicTime,
                                                                         @RequestParam(required = false) Integer lastViewCounts,
                                                                         @RequestParam(required = false) Integer lastReviewCounts,
                                                                         @PageableDefault(size = 5) Pageable pageable) {
        String sort = PageableUtil.getSortAsString(pageable);
        CaregiverProfileListResponse searchedProfiles = null;
        if (sort.equals("viewCounts")) {
            searchedProfiles = patientService.searchPageOrderByViews(condition, lastIndex, lastViewCounts, pageable);
        } else if (sort.equals("reviewCounts")) {
            // searchedProfiles = patientService.searchPageOrderByReviewCounts(condition, lastIndex, lastReviewCounts, pageable);
        } else {
            searchedProfiles = patientService.searchPageOrderByDefault(condition, lastIndex, lastProfilePublicTime, pageable);
        }
        return ResponseEntity.status(OK).body(searchedProfiles);
    }
}
