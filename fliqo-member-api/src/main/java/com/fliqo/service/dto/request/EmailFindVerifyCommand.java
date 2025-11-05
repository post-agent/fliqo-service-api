package com.fliqo.service.dto.request;

public record EmailFindVerifyCommand(String verificationId, String code) {
    public static EmailFindVerifyCommand of(String verificationId, String code) {
        return new EmailFindVerifyCommand(verificationId, code);
    }
}
