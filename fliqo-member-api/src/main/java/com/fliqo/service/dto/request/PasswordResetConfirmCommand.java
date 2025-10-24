package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record PasswordResetConfirmCommand(String resetToken, String newPassword) {
    public static PasswordResetConfirmCommand of(String resetToken, String newPassword) {
        return PasswordResetConfirmCommand.builder()
                .resetToken(resetToken)
                .newPassword(newPassword)
                .build();
    }
}
