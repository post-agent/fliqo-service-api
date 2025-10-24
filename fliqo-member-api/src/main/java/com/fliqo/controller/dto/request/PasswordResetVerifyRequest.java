package com.fliqo.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetVerifyRequest(@NotBlank String verificationId, @NotBlank String code) {}
