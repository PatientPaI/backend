package com.patientpal.backend.security.oauth;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.security.oauth.userinfo.CustomOAuth2UserInfo;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(MemberRepository memberRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var user = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        var userInfo = CustomOAuth2UserInfo.of(registrationId, user.getAttributes());

        Optional<Member> foundAccount = memberRepository.findByUsername(userInfo.getEmail());
        if (foundAccount.isEmpty()) {
            Member member = Member.builder()
                    .username(userInfo.getEmail())
                    .password(createDummyPassword())
                    .build();
            member.encodePassword(passwordEncoder);
            memberRepository.save(member);
            return new CustomOAuth2UserPrincipal(member, userInfo);
        }
        return new CustomOAuth2UserPrincipal(foundAccount.get(), userInfo);
    }

    private String createDummyPassword() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
