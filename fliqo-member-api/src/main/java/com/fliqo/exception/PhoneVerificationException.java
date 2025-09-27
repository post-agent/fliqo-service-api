package com.fliqo.exception;

public class PhoneVerificationException extends RuntimeException {
    private final PhoneVerifyError error;

    public PhoneVerificationException(PhoneVerifyError error) {
        super(error.message());
        this.error = error;
    }

    public PhoneVerifyError getError() {
        return error;
    }
}
