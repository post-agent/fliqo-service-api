package com.fliqo.service.dto.response;

public record PasswordResetStartResult(String verificationId, long expiresInSeconds) {}
