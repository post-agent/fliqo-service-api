package com.fliqo.service.dto.response;

import lombok.Builder;

@Builder
public record EmailFindResult(boolean found, String maskedEmail) {
    public static EmailFindResult found(String maskedEmail) {
        return EmailFindResult.builder().found(true).maskedEmail(maskedEmail).build();
    }

    public static EmailFindResult notFound() {
        return EmailFindResult.builder().found(false).maskedEmail(null).build();
    }
}
