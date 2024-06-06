package com.patientpal.backend.patient.controller;

import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/patient")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientProfileResponse> createPatientProfile(@AuthenticationPrincipal User currentMember,
                                                     @RequestBody @Valid PatientProfileCreateRequest patientProfileCreateRequest) {
        PatientProfileResponse patientProfileResponse = patientService.savePatientProfile(currentMember.getUsername(), patientProfileCreateRequest);
        return ResponseEntity.status(CREATED).body(patientProfileResponse);
    }

    @GetMapping
    public ResponseEntity<PatientProfileResponse> getPatientProfile(@AuthenticationPrincipal User currentMember) {
        return ResponseEntity.status(OK).body(patientService.getProfile(currentMember.getUsername()));
    }

    @PatchMapping
    public ResponseEntity<Void> updatePatientProfile(@AuthenticationPrincipal User currentMember,
                                                     @RequestBody @Valid PatientProfileUpdateRequest patientProfileUpdateRequest) {
        patientService.updatePatientProfile(currentMember.getUsername(), patientProfileUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * TODO
     *  - 프로필 삭제 후에는 매칭 시스템 이용 불가.
     *  - 유저가 매칭 중일때 프로필 삭제가 불가능하게("PENDING")하나라도 존재 시 프로필 삭제 불가능
     */
    @DeleteMapping //TODO
    public ResponseEntity<Void> deletePatientProfile(@AuthenticationPrincipal User currentMember) {
        patientService.deletePatientProfile(currentMember.getUsername());
        return ResponseEntity.noContent().build();
    }

}
