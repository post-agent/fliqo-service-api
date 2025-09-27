package com.fliqo.exception;

import java.util.function.Supplier;

public enum PhoneVerifyError implements Supplier<PhoneVerificationException> {
    INVALID_REQUEST("유효하지 않은 인증 요청입니다."),
    EXPIRED("인증 코드가 만료되었습니다."),
    ALREADY_PROCESSED("이미 처리된 인증 요청입니다."),
    ATTEMPTS_EXCEEDED("인증 시도 횟수를 초과했습니다."),
    CODE_MISMATCH("인증 코드가 올바르지 않습니다."),
    NOT_COMPLETED("휴대폰 인증이 완료되지 않았습니다."),
    PHONE_MISMATCH("인증된 휴대폰 번호와 제출된 번호가 일치하지 않습니다.");

    private final String message;

    PhoneVerifyError(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    @Override
    public PhoneVerificationException get() {
        return new PhoneVerificationException(this);
    }
}
