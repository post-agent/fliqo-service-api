package com.fliqo.service.dto.response;

import lombok.Builder;

public record EmailFindConfirmResult(String maskedEmail) {
    @Builder
    public EmailFindConfirmResult {}

    public static EmailFindConfirmResult of(String maskedEmail) {
        return EmailFindConfirmResult.builder()
                .maskedEmail(maskedEmail)
                .build();
    }
}
