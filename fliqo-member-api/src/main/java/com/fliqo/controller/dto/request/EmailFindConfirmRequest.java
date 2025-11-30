package com.fliqo.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailFindConfirmRequest(@NotBlank String verificationId, @NotBlank String code) {}
