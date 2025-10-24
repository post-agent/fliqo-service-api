package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record PasswordResetConfirmCommand(String resetToken, String newPassword) {}
