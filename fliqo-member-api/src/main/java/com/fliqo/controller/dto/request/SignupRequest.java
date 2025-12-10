package com.fliqo.controller.dto.request;

import java.util.List;

import com.fliqo.service.dto.request.SignupCommand;
import com.fliqo.service.dto.request.TermsAgreementCommand;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String passwordConfirm,
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @NotBlank String phoneVerificationToken,
        List<TermsAgreementRequest> agreements) {
    public SignupCommand toCommand() {
        return SignupCommand.builder()
                .email(email)
                .rawPassword(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .phoneVerificationToken(phoneVerificationToken)
                .agreements(
                        agreements == null
                                ? null
                                : agreements.stream()
                                        .map(a -> new TermsAgreementCommand(a.code(), a.agreed()))
                                        .toList())
                .build();
    }
}
