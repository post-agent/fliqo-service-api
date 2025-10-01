package com.fliqo.controller.dto.response;

public record TokenResponseDto(String tokenType, String accessToken, long expirationInSeconds) {
    public static TokenResponseDto of(String accessToken, long expirationInSeconds) {
        return new TokenResponseDto("Bearer", accessToken, expirationInSeconds);
    }
}
