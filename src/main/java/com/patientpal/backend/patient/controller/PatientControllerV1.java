package com.patientpal.backend.patient.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.patientpal.backend.image.dto.ImageNameDto;
import com.patientpal.backend.image.service.PresignedUrlService;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/patient")
public class PatientControllerV1 {

    private final PatientService patientService;
    private final PresignedUrlService presignedUrlService;
    private String path;

    @PostMapping
    public ResponseEntity<PatientProfileResponse> createPatientProfile(@AuthenticationPrincipal User currentMember,
                                                     @RequestBody @Valid PatientProfileCreateRequest patientProfileCreateRequest) {
        String profileImageUrl = presignedUrlService.findByName(path);
        PatientProfileResponse patientProfileResponse = patientService.savePatientProfile(currentMember.getUsername(), patientProfileCreateRequest, profileImageUrl);
        return ResponseEntity.status(CREATED).body(patientProfileResponse);
    }

    @PostMapping("/presigned")
    public String createPresigned(@RequestBody ImageNameDto imageNameDto) {
        path = "profiles";
        String imageName = imageNameDto.getImageName();
        return presignedUrlService.getPresignedUrl(path, imageName);
    }

    @GetMapping
    public ResponseEntity<PatientProfileResponse> getPatientProfile(@AuthenticationPrincipal User currentMember) {
        return ResponseEntity.status(OK).body(patientService.getProfile(currentMember.getUsername()));
    }

    @PatchMapping
    public ResponseEntity<Void> updatePatientProfile(@AuthenticationPrincipal User currentMember,
                                                     @RequestBody @Valid PatientProfileUpdateRequest patientProfileUpdateRequest) {
        String profileImageUrl = presignedUrlService.findByName(path);
        patientService.updatePatientProfile(currentMember.getUsername(), patientProfileUpdateRequest, profileImageUrl);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePatientProfile(@AuthenticationPrincipal User currentMember) {
        patientService.deletePatientProfile(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/image")
    public ResponseEntity<Void> deletePatientProfileImage(@AuthenticationPrincipal User currentMember) {
        patientService.deletePatientProfileImage(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("register/toMatchList")
    public ResponseEntity<Void> registerPatientProfileToMatchList(@AuthenticationPrincipal User currentMember) {
        patientService.registerPatientProfileToMatchList(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("unregister/toMatchList")
    public ResponseEntity<Void> unregisterPatientProfileToMatchList(@AuthenticationPrincipal User currentMember) {
        patientService.unregisterPatientProfileToMatchList(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }
}
