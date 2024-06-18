package com.patientpal.backend.caregiver.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileDetailResponse;
import com.patientpal.backend.caregiver.service.CaregiverService;
import com.patientpal.backend.image.dto.ImageNameDto;
import com.patientpal.backend.image.service.PresignedUrlService;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.patientpal.backend.patient.dto.response.PatientProfileListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "간병인", description = "간병인 프로필 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/caregiver")
public class CaregiverControllerV1 {

    private final CaregiverService caregiverService;
    private final PresignedUrlService presignedUrlService;

    @Operation(summary = "간병인 프로필 생성", description = "간병인 프로필을 새로 생성합니다. 선택적으로 이미지를 업로드할 수 있습니다.")
    @ApiResponse(responseCode = "201", description = "간병인 프로필 생성 성공", content = @Content(schema = @Schema(implementation = CaregiverProfileDetailResponse.class)))
    @PostMapping
    public ResponseEntity<CaregiverProfileDetailResponse> createCaregiverProfile(
            @AuthenticationPrincipal User currentMember,
            @RequestBody @Valid CaregiverProfileCreateRequest caregiverProfileCreateRequest,
            @RequestParam(required = false) String profileImageUrl) {
        CaregiverProfileDetailResponse caregiverProfileDetailResponse = caregiverService.saveCaregiverProfile(currentMember.getUsername(), caregiverProfileCreateRequest, presignedUrlService.getSavedUrl(profileImageUrl));
        return ResponseEntity.status(CREATED).body(caregiverProfileDetailResponse);
    }

    @Operation(summary = "AWS S3에 저장될 전체 URL 경로 생성", description = "이미지 업로드를 위한 URL을 생성합니다. 생성된 URL로 파일 첨부 후 PUTMAPPING 진행 시 이미지가 저장됩니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공")
    @PostMapping("/presigned")
    public String createPresigned(@RequestBody ImageNameDto imageNameDto) {
        String imageName = imageNameDto.getImageName();
        return presignedUrlService.getPresignedUrl("profiles", imageName);
    }

    @Operation(summary = "간병인 프로필 조회", description = "현재 로그인된 사용자의 간병인 프로필을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "간병인 프로필 조회 성공", content = @Content(schema = @Schema(implementation = CaregiverProfileDetailResponse.class)))
    @GetMapping
    public ResponseEntity<CaregiverProfileDetailResponse> getCaregiverProfile(
            @AuthenticationPrincipal User currentMember) {
        return ResponseEntity.status(OK).body(caregiverService.getProfile(currentMember.getUsername()));
    }

    @Operation(summary = "간병인 프로필 수정", description = "간병인 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "간병인 프로필 수정 성공")
    @PatchMapping
    public ResponseEntity<Void> updateCaregiverProfile(
            @AuthenticationPrincipal User currentMember,
            @RequestBody @Valid CaregiverProfileUpdateRequest caregiverProfileUpdateRequest,
            @RequestParam(required = false) String profileImageUrl) {
        caregiverService.updateCaregiverProfile(currentMember.getUsername(), caregiverProfileUpdateRequest, presignedUrlService.getSavedUrl(profileImageUrl));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "간병인 프로필 삭제", description = "간병인 프로필을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "간병인 프로필 삭제 성공")
    @DeleteMapping
    public ResponseEntity<Void> deleteCaregiverProfile(
            @AuthenticationPrincipal User currentMember) {
        caregiverService.deleteCaregiverProfile(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "간병인 프로필 이미지 삭제", description = "간병인 프로필에서 이미지를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "간병인 프로필 이미지 삭제 성공")
    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteCaregiverProfileImage(
            @AuthenticationPrincipal User currentMember) {
        caregiverService.deleteCaregiverProfileImage(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "간병인 프로필 매칭 리스트에 등록", description = "간병인 프로필을 매칭 리스트에 등록합니다.")
    @ApiResponse(responseCode = "204", description = "간병인 프로필 매칭 리스트 등록 성공")
    @PostMapping("register/toMatchList")
    public ResponseEntity<Void> registerCaregiverProfileToMatchList(
            @AuthenticationPrincipal User currentMember) {
        caregiverService.registerCaregiverProfileToMatchList(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "간병인 프로필 매칭 리스트에서 제거", description = "간병인 프로필을 매칭 리스트에서 제거합니다.")
    @ApiResponse(responseCode = "204", description = "간병인 프로필 매칭 리스트 제거 성공")
    @PostMapping("unregister/toMatchList")
    public ResponseEntity<Void> unregisterCaregiverProfileToMatchList(
            @AuthenticationPrincipal User currentMember) {
        caregiverService.unregisterCaregiverProfileToMatchList(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "환자 찾기", description = "지역, 이름, 성별, 나이로 환자를 검색합니다. sort='field',asc/desc로 정렬 가능합니다. ")
    @ApiResponse(responseCode = "200", description = "조건에 해당하는 환자 찾기 성공")
    @GetMapping("/search")
    public ResponseEntity<PatientProfileListResponse> searchPatients(ProfileSearchCondition condition,
                                                                       @PageableDefault(size = 5) Pageable pageable) {
        final PatientProfileListResponse searchedProfiles = caregiverService.searchPageOrderBy(condition, pageable);
        return ResponseEntity.status(OK).body(searchedProfiles);
    }
}
