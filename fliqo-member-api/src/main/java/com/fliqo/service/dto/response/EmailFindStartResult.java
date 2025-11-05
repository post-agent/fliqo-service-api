package com.fliqo.service.dto.response;

import lombok.Builder;

@Builder
public record EmailFindStartResult(String verificationId, long expiresInMinutes) {
    public static EmailFindStartResult of(String verificationId, long expiresInMinutes) {
        return EmailFindStartResult.builder()
                .verificationId(verificationId)
                .expiresInMinutes(expiresInMinutes)
                .build();
    }
}
