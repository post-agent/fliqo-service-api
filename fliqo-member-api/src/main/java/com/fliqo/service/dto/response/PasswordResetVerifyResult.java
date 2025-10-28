package com.fliqo.service.dto.response;

import lombok.Builder;

@Builder
public record PasswordResetVerifyResult(String resetToken, long expiresInSeconds) {
    public static PasswordResetVerifyResult of(String resetToken, long expiresInSeconds) {
        return PasswordResetVerifyResult.builder()
                .resetToken(resetToken)
                .expiresInSeconds(expiresInSeconds)
                .build();
    }
}
