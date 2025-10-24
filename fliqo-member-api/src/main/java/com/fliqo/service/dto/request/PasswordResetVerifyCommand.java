package com.fliqo.service.dto.request;

public record PasswordResetVerifyCommand(String verificationId, String code) {}
