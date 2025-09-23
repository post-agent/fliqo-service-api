package com.fliqo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCheckRequest(
        @NotBlank(message = "이메일을 입력하세요.")
        @Email(message = "잘못된 이메일 형식")
        String email
) {
}
