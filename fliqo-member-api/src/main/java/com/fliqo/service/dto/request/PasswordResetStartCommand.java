package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record PasswordResetStartCommand(String email, String phoneNumber) {}
