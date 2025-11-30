package com.fliqo.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
        @Min(1) long accessMin, // 액세스 토큰 유효 시간(분)
        @Min(1) long refreshDay, // 리프레시 토큰 유효 시간(일)
        Map<String, ProviderConfig> providers, // OAuth 제공자별 설정
        RedirectConfig redirect, // OAuth 리다이렉트 설정
        String baseUrl) {

    /** provider 이름(kakao/naver 등)에 해당하는 설정 조회 */
    public ProviderConfig provider(String providerName) {
        if (providers == null) {
            return null;
        }
        return providers.get(providerName.toLowerCase());
    }

    public record ProviderConfig(
            String clientId,
            String clientSecret,
            String authorizationUri,
            String tokenUri,
            String userInfoUri) {}

    public record RedirectConfig(
            String successUrl, // 프론트 성공 URL
            String failureUrl // 프론트 실패 URL
            ) {}
}
