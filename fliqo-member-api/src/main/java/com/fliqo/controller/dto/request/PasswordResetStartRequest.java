package com.fliqo.controller.dto.request;

import com.fliqo.domain.entity.PhoneVerificationPurpose;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PasswordResetStartRequest(
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotNull PhoneVerificationPurpose purpose) {}
