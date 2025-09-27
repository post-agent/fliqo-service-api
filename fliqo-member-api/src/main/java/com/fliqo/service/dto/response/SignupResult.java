package com.fliqo.service.dto.response;

import lombok.Builder;

@Builder
public record SignupResult(
        Long memberId, String memberUuid, String email, String name, String phoneNumber) {}
