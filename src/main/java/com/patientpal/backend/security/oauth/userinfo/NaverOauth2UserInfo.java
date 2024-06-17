package com.patientpal.backend.security.oauth.userinfo;

import static com.patientpal.backend.common.utils.ConvertUtils.uncheckedCast;

import java.util.Map;

public class NaverOauth2UserInfo extends CustomOauth2UserInfo {

    protected NaverOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public void initProperties() {
        Map<String, Object> naverAccount = uncheckedCast(attributes.get("response"));
        email = naverAccount.get("email").toString();
        name = naverAccount.get("nickname").toString();
    }

    @Override
    public CustomOauth2Provider getProvider() {
        return CustomOauth2Provider.NAVER;
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
