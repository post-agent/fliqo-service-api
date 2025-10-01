package com.fliqo.exception;

public class MemberException extends RuntimeException {
    private final MemberError error;

    public MemberException(MemberError error) {
        super(error.message());
        this.error = error;
    }

    public MemberError getError() {
        return error;
    }
}
