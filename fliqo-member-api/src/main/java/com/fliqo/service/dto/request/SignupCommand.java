package com.fliqo.service.dto.request;

import java.util.List;

import lombok.Builder;

@Builder
public record SignupCommand(
        String email,
        String rawPassword,
        String name,
        String phoneNumber,
        String phoneVerificationToken,
        List<TermsAgreementCommand> agreements) {}
