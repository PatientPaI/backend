package com.patientpal.backend.member.controller;


import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.service.CaregiverService;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.dto.CaregiverCompleteProfileResponse;
import com.patientpal.backend.member.dto.MemberDetailResponse;
import com.patientpal.backend.member.dto.PatientCompleteProfileResponse;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.service.PatientService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final PatientService patientService;
    private final CaregiverService caregiverService;
    private final MemberRepository memberRepository;

    @GetMapping("/information")
    public ResponseEntity<?> getIsCompleteProfile(@AuthenticationPrincipal User user) {
        Member member = memberService.getUserByUsername(user.getUsername());
        if (member.getRole() == Role.USER) {
            Patient patient = patientService.getPatientByMemberId(member.getId());
            return ResponseEntity.status(HttpStatus.OK).body(
                    PatientCompleteProfileResponse.of(patient.getId(), patient.getName(), patient.getAge(), patient.getContact(), patient.getGender(),
                            patient.getAddress(), patient.getIsNok(), patient.getNokName(), patient.getNokContact(), patient.getRealCarePlace(), patient.getPatientSignificant(),
                            patient.getCareRequirements(), patient.getIsCompleteProfile(), patient.getIsProfilePublic(), patient.getProfileImageUrl(), patient.getViewCounts(),
                            patient.getWantCareStartDate(), patient.getWantCareEndDate()));
        } else if (member.getRole() == Role.CAREGIVER) {
            Caregiver caregiver = caregiverService.getCaregiverByMemberId(member.getId());
            return ResponseEntity.status(HttpStatus.OK).body(
                    CaregiverCompleteProfileResponse.of(caregiver.getId(), caregiver.getName(), caregiver.getAge(), caregiver.getContact(), caregiver.getGender(), caregiver.getAddress(),
                            caregiver.getRating(), caregiver.getExperienceYears(), caregiver.getSpecialization(), caregiver.getCaregiverSignificant(), caregiver.getIsCompleteProfile(),
                            caregiver.getIsProfilePublic(), caregiver.getProfileImageUrl(), caregiver.getViewCounts(), caregiver.getWantCareStartDate(), caregiver.getWantCareEndDate()));
        }
        throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
    }

    @GetMapping("/isProfilePublic")
    public ResponseEntity<Boolean> getIsProfilePublic(@AuthenticationPrincipal User user) {
        Member member = memberService.getUserByUsername(user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(member.getIsProfilePublic());
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameAvailability(@RequestParam String username) {
        return ResponseEntity.ok(memberService.existsByUsername(username));
    }

    @GetMapping
    public List<MemberDetailResponse> list(@RequestParam("memberIds") List<Long> memberIds) {
        List<Member> members =  memberService.getMembers(memberIds);
        return members.stream()
                .map(MemberDetailResponse::of)
                .collect(Collectors.toList());
    }
}
