package com.fliqo.controller.dto.response;

import lombok.Builder;

public record EmailFindConfirmResponse(String maskedEmail) {
    @Builder
    public EmailFindConfirmResponse {}

    public static EmailFindConfirmResponse of(String maskedEmail) {
        return EmailFindConfirmResponse.builder()
                .maskedEmail(maskedEmail)
                .build();
    }
}
