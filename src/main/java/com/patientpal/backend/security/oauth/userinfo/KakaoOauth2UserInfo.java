package com.patientpal.backend.security.oauth.userinfo;

import static com.patientpal.backend.common.utils.ConvertUtils.uncheckedCast;

import java.util.Map;

public class KakaoOauth2UserInfo extends CustomOauth2UserInfo {

    protected KakaoOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public void initProperties() {
        Map<String, Object> kakaoAccount = uncheckedCast(attributes.get("kakao_account"));
        email = kakaoAccount.get("email").toString();

        Map<String, Object> profile = uncheckedCast(kakaoAccount.get("profile"));
        name = profile.get("nickname").toString();
    }

    @Override
    public CustomOauth2Provider getProvider() {
        return CustomOauth2Provider.KAKAO;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }
}
