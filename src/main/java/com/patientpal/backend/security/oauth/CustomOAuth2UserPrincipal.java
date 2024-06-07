package com.patientpal.backend.security.oauth;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.security.oauth.userinfo.CustomOAuth2UserInfo;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserPrincipal extends User implements OAuth2User {
    private transient CustomOAuth2UserInfo userInfo;

    public CustomOAuth2UserPrincipal(Member member) {
        super(member.getUsername(), member.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public CustomOAuth2UserPrincipal(Member member, CustomOAuth2UserInfo userInfo) {
        this(member);
        this.userInfo = userInfo;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return userInfo.getAttributes();
    }

    @Override
    public String getName() {
        return userInfo.getName();
    }
}
