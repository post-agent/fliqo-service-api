package com.fliqo.controller.dto.request;

import com.fliqo.domain.entity.PhoneVerificationPurpose;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PhoneVerifyStartRequest(
        @NotBlank @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
                String phoneNumber,
        @NotNull PhoneVerificationPurpose purpose) {}
