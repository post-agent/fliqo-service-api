package com.fliqo.service.dto.request;

public record EmailFindStartCommand(String phoneNumber) {
    public static EmailFindStartCommand of(String phoneNumber) {
        return new EmailFindStartCommand(phoneNumber);
    }
}
