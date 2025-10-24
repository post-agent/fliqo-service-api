package com.fliqo.service.dto.response;

public record PasswordResetVerifyResult(String resetToken, long expiresInSeconds) {}
