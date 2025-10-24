package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record PasswordResetVerifyCommand(String verificationId, String code) {}
