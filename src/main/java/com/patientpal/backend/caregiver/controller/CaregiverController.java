package com.patientpal.backend.caregiver.controller;

import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.service.CaregiverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/caregiver")
public class CaregiverController {

    private final CaregiverService caregiverService;

    @PostMapping
    public ResponseEntity<CaregiverProfileResponse> createCaregiverProfile(@AuthenticationPrincipal User currentMember,
                                                     @RequestBody @Valid CaregiverProfileCreateRequest CaregiverProfileCreateRequest) {
        CaregiverProfileResponse caregiverProfileResponse = caregiverService.saveCaregiverProfile(currentMember.getUsername(), CaregiverProfileCreateRequest);
        return ResponseEntity.status(CREATED).body(caregiverProfileResponse);
    }

    @GetMapping
    public ResponseEntity<CaregiverProfileResponse> getCaregiverProfile(@AuthenticationPrincipal User currentMember) {
        return ResponseEntity.status(OK).body(caregiverService.getProfile(currentMember.getUsername()));
    }

    @PatchMapping
    public ResponseEntity<Void> updateCaregiverProfile(@AuthenticationPrincipal User currentMember,
                                                       @RequestBody @Valid CaregiverProfileUpdateRequest caregiverProfileUpdateRequest) {
        caregiverService.updateCaregiverProfile(currentMember.getUsername(), caregiverProfileUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping //TODO 프로필 삭제 후에는 매칭 시스템 이용 불가.
    public ResponseEntity<Void> deleteCaregiverProfile(@AuthenticationPrincipal User currentMember) {
        caregiverService.deleteCaregiverProfile(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

}
