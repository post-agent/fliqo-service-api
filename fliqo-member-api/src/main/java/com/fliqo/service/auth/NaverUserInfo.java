package com.fliqo.service.auth;

import java.util.Map;

import com.fliqo.domain.entity.AuthProvider;

/** 네이버 사용자 정보 구현체 네이버 API 응답을 파싱하여 표준 인터페이스로 변환 */
public record NaverUserInfo(Map<String, Object> attributes) implements AuthUserInfo {

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.NAVER;
    }

    @Override
    public String getProviderId() {
        return getNestedValue("response", "id");
    }

    @Override
    public String getEmail() {
        return getNestedValue("response", "email");
    }

    @Override
    public String getName() {
        return getNestedValue("response", "name");
    }

    @Override
    public String getNickname() {
        return getNestedValue("response", "nickname");
    }

    @Override
    public String getPhone() {
        return getNestedValue("response", "mobile");
    }

    @Override
    public String getProfileImageUrl() {
        return getNestedValue("response", "profile_image");
    }
}
