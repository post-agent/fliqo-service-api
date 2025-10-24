package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record PasswordResetStartCommand(String email, String phoneNumber) {
    public static PasswordResetStartCommand of(String email, String phoneNumber) {
        return PasswordResetStartCommand.builder().email(email).phoneNumber(phoneNumber).build();
    }
}
