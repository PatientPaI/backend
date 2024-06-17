package com.patientpal.backend.security.oauth.userinfo;

import lombok.Getter;

@Getter
public enum CustomOauth2Provider {
    NAVER("naver"),
    KAKAO("kakao"),
    GOOGLE("google");

    private final String provider;

    CustomOauth2Provider(String provider) {
        this.provider = provider;
    }

    public boolean equalsWith(String provider) {
        return this.provider.equalsIgnoreCase(provider);
    }
}
