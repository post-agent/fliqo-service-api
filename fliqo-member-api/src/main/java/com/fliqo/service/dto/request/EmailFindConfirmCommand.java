package com.fliqo.service.dto.request;

import lombok.Builder;

public record EmailFindConfirmCommand(String verificationId, String code) {
    @Builder
    public EmailFindConfirmCommand {}

    public static EmailFindConfirmCommand of(String verificationId, String code) {
        return EmailFindConfirmCommand.builder().verificationId(verificationId).code(code).build();
    }
}
