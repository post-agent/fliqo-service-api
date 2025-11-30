package com.fliqo.service.dto.request;

import com.fliqo.domain.entity.PhoneVerificationPurpose;

import lombok.Builder;

@Builder
public record PasswordResetStartCommand(
        String email, String phoneNumber, PhoneVerificationPurpose purpose) {
    public static PasswordResetStartCommand of(
            String email, String phoneNumber, PhoneVerificationPurpose purpose) {
        return PasswordResetStartCommand.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .purpose(purpose)
                .build();
    }
}
