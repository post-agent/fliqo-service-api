package com.fliqo.service.dto;

public record EmailCheckCommand(String email) {
    public EmailCheckCommand {
        if (email != null) email = email.trim();
    }
}
