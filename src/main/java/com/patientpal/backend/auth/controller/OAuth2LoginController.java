package com.patientpal.backend.auth.controller;

import com.patientpal.backend.auth.domain.SocialProvider;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.SocialDataService;
import com.patientpal.backend.auth.service.SocialLoginService;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpRequest;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginController {


    private final JwtLoginService jwtLoginService;
    private final MemberService memberService;
    private final SocialDataService socialDataService;
    private final SocialLoginService socialLoginService;


    @Operation(summary = "소셜 로그인 URL 가져오기", description = "소셜 로그인 페이지 URL을 JSON으로 응답합니다.")
    @ApiResponse(responseCode = "200", description = "소셜 로그인 URL 반환 성공")
    @GetMapping("/oauth2/authorize/{provider}")
    public ResponseEntity<Map<String, String>> getSocialLoginUrl(@PathVariable SocialProvider provider) {
        String loginUrl = socialLoginService.getLoginUrl(provider);

        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", loginUrl);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "구글 엑세스 토큰 요청", description = "구글 인가 코드를 사용하여 엑세스 토큰을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 요청 성공",
            content = @Content(schema = @Schema(implementation = TokenDto.class)))
    @PostMapping("/oauth2/token/google")
    public ResponseEntity<TokenDto> getGoogleToken(@RequestParam("code") String code) {
        TokenDto tokenDto = jwtLoginService.getGoogleAccessToken(code);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "카카오 엑세스 토큰 요청", description = "카카오 인가 코드를 사용하여 엑세스 토큰을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 요청 성공",
            content = @Content(schema = @Schema(implementation = TokenDto.class)))
    @PostMapping("/oauth2/token/kakao")
    public ResponseEntity<TokenDto> getKakaoToken(@RequestParam("code") String code) {
        TokenDto tokenDto = jwtLoginService.getKakaoAccessTokenFromCode(code);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "네이버 엑세스 토큰 요청", description = "네이버 인가 코드를 사용하여 엑세스 토큰을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 요청 성공",
            content = @Content(schema = @Schema(implementation = TokenDto.class)))
    @PostMapping("/oauth2/token/naver")
    public ResponseEntity<TokenDto> getNaverToken(@RequestParam("code") String code, @RequestParam("state") String state) {
        TokenDto tokenDto = jwtLoginService.getNaverAccessTokenFromCode(code, state);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "액세스 토큰으로 소셜 데이터 요청", description = "액세스 토큰을 사용하여 소셜 플랫폼에서 사용자 데이터를 요청합니다.")
    @ApiResponse(responseCode = "200", description = "소셜 데이터 요청 성공",
            content = @Content(schema = @Schema(implementation = String.class))) 
    @GetMapping("/user/data")
    public ResponseEntity<String> getUserData(@RequestParam String accessToken, @RequestParam String provider) {
        String userData = socialDataService.getUserData(accessToken, provider);
        return ResponseEntity.ok(userData);
    }

    @PostMapping("/oauth2/register-or-login")
    @Operation(summary = "소셜 회원가입 또는 로그인", description = "소셜 로그인 후 회원가입 또는 로그인 절차를 수행한다.")
    @ApiResponse(responseCode = "200", description = "회원가입 또는 로그인 성공",
            content = @Content(schema = @Schema(implementation = Oauth2SignUpResponse.class)))
    public ResponseEntity<?> processOauth2Signup(HttpSession session) {
        String username = (String) session.getAttribute("username");

        Optional<Member> optionalMember = memberService.findOptionalByUsername(username);
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return socialLoginService.createLoginResponse(session, member);
        }

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/oauth2/register");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth2/register")
    @Operation(summary = "소셜 회원가입 정보 가져오기", description = "소셜 로그인 후 회원가입 페이지에서 필요한 정보를 가져온다.")
    @ApiResponse(responseCode = "200", description = "소셜 회원가입 정보 가져오기 성공",
            content = @Content(schema = @Schema(implementation = Oauth2SignUpResponse.class)))
    public ResponseEntity<Oauth2SignUpResponse> getOauth2UserInfo(HttpSession session) {
        Oauth2SignUpResponse response = socialLoginService.createOauth2SignUpResponse(session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oauth2/register")
    @Operation(summary = "소셜 회원가입", description = "소셜 로그인 후 추가 정보를 입력하여 회원가입을 처리한다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = Oauth2SignUpResponse.class)))
    public ResponseEntity<Oauth2SignUpResponse> registerUser(@RequestBody Oauth2SignUpRequest signupForm, HttpSession session) {

        Long newMemberId = memberService.saveSocialUser(signupForm);
        Member newMember = memberService.getUserById(newMemberId);

        return socialLoginService.createSignupResponse(signupForm, session, newMember);
    }
}
