package com.fliqo.service.auth;

import java.util.Map;

import com.fliqo.domain.entity.AuthProvider;

public interface AuthUserInfo {

    Map<String, Object> attributes();

    // 중첩된 Map에서 안전하게 값 가져오기
    @SuppressWarnings("unchecked")
    default <T> T getNestedValue(String... keys) {
        Map<String, Object> current = attributes();
        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (!(next instanceof Map)) {
                return null;
            }
            current = (Map<String, Object>) next;
        }
        return (T) current.get(keys[keys.length - 1]);
    }

    /** 제공자 타입 반환 (KAKAO, NAVER) */
    AuthProvider getProvider();

    /** 제공자에서 발급한 고유 ID */
    String getProviderId();

    /** 이메일 (선택적, null 가능) */
    String getEmail();

    /** 실명 (선택적, null 가능) */
    String getName();

    /** 닉네임 (선택적, null 가능) */
    String getNickname();

    /** 전화번호 (선택적, null 가능) */
    String getPhone();

    /** 프로필 이미지 URL (선택적, null 가능) */
    String getProfileImageUrl();
}
