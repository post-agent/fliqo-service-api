package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record EmailCheckCommand(String email) {
    public EmailCheckCommand {
        if (email != null) email = email.trim().toLowerCase();
    }

    public static EmailCheckCommand of(String email) {
        return EmailCheckCommand.builder().email(email).build();
    }
}
