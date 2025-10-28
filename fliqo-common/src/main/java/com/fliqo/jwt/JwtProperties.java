package com.fliqo.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        @NotBlank String issuer,
        @NotBlank String audience,
        @NotBlank String secret, // Base64 인코딩 키
        HeaderInjection headerInjection) {
    public record HeaderInjection(
            boolean enabled,
            String userIdClaim, // 기본 사용: "sub"
            String rolesClaim // 기본 사용: "roles"
            ) {}
}
