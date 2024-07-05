package com.patientpal.backend.security.oauth;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.security.oauth.userinfo.CustomOauth2UserInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOauth2UserPrincipal extends User implements OAuth2User {
    private transient CustomOauth2UserInfo userInfo;
    private String registrationId;

    public CustomOauth2UserPrincipal(Member member) {
        super(member.getUsername(), member.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public CustomOauth2UserPrincipal(Member member, CustomOauth2UserInfo userInfo) {
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

    public CustomOauth2UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
    public void setUserInfo(CustomOauth2UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
