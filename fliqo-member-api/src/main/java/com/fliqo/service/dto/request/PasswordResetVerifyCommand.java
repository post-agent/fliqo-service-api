package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record PasswordResetVerifyCommand(String verificationId, String code) {
    public static PasswordResetVerifyCommand of(String verificationId, String code) {
        return PasswordResetVerifyCommand.builder()
                .verificationId(verificationId)
                .code(code)
                .build();
    }
}
