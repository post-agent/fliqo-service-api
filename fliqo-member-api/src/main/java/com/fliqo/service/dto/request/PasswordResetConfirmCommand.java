package com.fliqo.service.dto.request;

public record PasswordResetConfirmCommand(String resetToken, String newPassword) {}
