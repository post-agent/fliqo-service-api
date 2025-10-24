package com.fliqo.service.dto.request;

public record PasswordResetStartCommand(String email, String phoneNumber) {}
