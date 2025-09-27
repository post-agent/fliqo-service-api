package com.fliqo.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PhoneVerifyConfirmRequest(@NotBlank String verificationId, @NotBlank String code) {}
