package com.patientpal.backend.caregiver.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/caregiver")
public class CaregiverControllerV1 {

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

    @DeleteMapping
    public ResponseEntity<Void> deleteCaregiverProfile(@AuthenticationPrincipal User currentMember) {
        caregiverService.deleteCaregiverProfile(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("register/toMatchList")
    public ResponseEntity<Void> registerCaregiverProfileToMatchList(@AuthenticationPrincipal User currentMember) {
        caregiverService.registerCaregiverProfileToMatchList(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("unregister/toMatchList")
    public ResponseEntity<Void> unregisterCaregiverProfileToMatchList(@AuthenticationPrincipal User currentMember) {
        caregiverService.unregisterCaregiverProfileToMatchList(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }
}
