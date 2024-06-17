package com.patientpal.backend.security.oauth.userinfo;

import java.util.Map;

public class GoogleOauth2UserInfo extends CustomOauth2UserInfo {

    protected GoogleOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public void initProperties() {
        email = attributes.get("email").toString();
        name = attributes.get("name").toString();
    }

    @Override
    public CustomOauth2Provider getProvider() {
        return CustomOauth2Provider.GOOGLE;
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
