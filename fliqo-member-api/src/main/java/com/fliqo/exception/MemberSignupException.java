package com.fliqo.exception;

public class MemberSignupException extends RuntimeException {
    private final MemberSignupError error;

    public MemberSignupException(MemberSignupError error) {
        super(error.message());
        this.error = error;
    }

    public MemberSignupError getError() {
        return error;
    }
}
