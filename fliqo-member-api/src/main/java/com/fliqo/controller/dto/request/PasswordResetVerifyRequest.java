package com.fliqo.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PasswordResetVerifyRequest(@NotBlank String verificationId, @NotBlank String code) {}
