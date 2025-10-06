package com.fliqo.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDto(@Email @NotBlank String email, @NotBlank String password) {}
