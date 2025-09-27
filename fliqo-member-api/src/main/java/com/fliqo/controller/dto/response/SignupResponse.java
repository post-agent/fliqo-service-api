package com.fliqo.controller.dto.response;

import lombok.Builder;

@Builder
public record SignupResponse(String memberUuid, String email, String name, String phoneNumber) {}
