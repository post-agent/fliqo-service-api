package com.fliqo.domain.entity;

import java.util.Map;

import com.fliqo.exception.BadRequestException;
import com.fliqo.exception.ErrorCode;
import com.fliqo.service.auth.AuthUserInfo;
import com.fliqo.service.auth.KakaoUserInfo;
import com.fliqo.service.auth.NaverUserInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    LOCAL("일반 회원가입"),
    KAKAO("카카오"),
    NAVER("네이버");

    private final String description;

    /** 소셜 로그인 제공자인지 확인 */
    public boolean isSocial() {
        return this != LOCAL;
    }

    public AuthUserInfo createUserInfo(Map<String, Object> attributes) {
        return switch (this) {
            case KAKAO -> new KakaoUserInfo(attributes);
            case NAVER -> new NaverUserInfo(attributes);
            case LOCAL -> throw new BadRequestException(ErrorCode.AUTH_PROVIDER_NOT_SUPPORTED);
        };
    }
}
