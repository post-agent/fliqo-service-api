package com.fliqo.controller.dto.response;

import lombok.Builder;

@Builder
public record EmailFindStartResponse(String verificationId, long expiresInMinutes) {
    public static EmailFindStartResponse of(String verificationId, long expiresInMinutes) {
        return EmailFindStartResponse.builder()
                .verificationId(verificationId)
                .expiresInMinutes(expiresInMinutes)
                .build();
    }
}
