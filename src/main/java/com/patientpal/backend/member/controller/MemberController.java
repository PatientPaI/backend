package com.patientpal.backend.member.controller;


import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.dto.MemberCompleteProfileResponse;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/member/information")
    public ResponseEntity<MemberCompleteProfileResponse> getIsCompleteProfile(@AuthenticationPrincipal User user) {
        Member member = memberService.getUserByUsername(user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(MemberCompleteProfileResponse.of(member.getId(), member.getName(), member.getIsCompleteProfile()));
    }
}
