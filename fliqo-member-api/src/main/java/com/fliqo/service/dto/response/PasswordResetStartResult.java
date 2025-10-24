package com.fliqo.service.dto.response;

public record PasswordResetStartResult(String verificationId, long expiresInSeconds) {
    public static PasswordResetStartResult of(String verificationId, long expiresInSeconds) {
        return new PasswordResetStartResult(verificationId, expiresInSeconds);
    }
}
