package com.fliqo.controller.dto.response;

import lombok.Builder;

@Builder
public record EmailFindVerifyResponse(boolean found, String maskedEmail, String message) {
    public static EmailFindVerifyResponse of(boolean found, String maskedEmail, String message) {
        return EmailFindVerifyResponse.builder()
                .found(found)
                .maskedEmail(maskedEmail)
                .message(message)
                .build();
    }
}
