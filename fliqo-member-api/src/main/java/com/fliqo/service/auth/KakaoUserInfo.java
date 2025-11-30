package com.fliqo.service.auth;

import java.util.Map;

import com.fliqo.domain.entity.AuthProvider;

/** 카카오 사용자 정보 구현체 카카오 API 응답을 파싱하여 표준 인터페이스로 변환 */
public record KakaoUserInfo(Map<String, Object> attributes) implements AuthUserInfo {

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getEmail() {
        return getNestedValue("kakao_account", "email");
    }

    @Override
    public String getName() {
        return getNestedValue("kakao_account", "name");

        // 현재는 nickname을 name으로 사용
        //        return getNestedValue("properties", "nickname");
    }

    @Override
    public String getNickname() {
        return getNestedValue("properties", "nickname");
    }

    @Override
    public String getPhone() {
        return getNestedValue("kakao_account", "phone_number");

        //        return null;
    }

    @Override
    public String getProfileImageUrl() {
        // properties 안에 profile_image
        return getNestedValue("properties", "profile_image");
    }
}
