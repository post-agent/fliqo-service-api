package com.fliqo.service.dto.response;

import lombok.Builder;

@Builder
public record PasswordResetStartResult(String verificationId, long expiresInSeconds) {
    public static PasswordResetStartResult of(String verificationId, long expiresInSeconds) {
        return PasswordResetStartResult.builder()
                .verificationId(verificationId)
                .expiresInSeconds(expiresInSeconds)
                .build();
    }
}
