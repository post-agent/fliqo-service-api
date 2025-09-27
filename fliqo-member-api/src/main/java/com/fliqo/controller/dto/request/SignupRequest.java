package com.fliqo.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String passwordConfirm,
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @NotBlank String phoneVerificationToken) {}
