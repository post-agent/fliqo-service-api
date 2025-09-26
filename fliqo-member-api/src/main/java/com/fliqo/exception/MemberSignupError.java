package com.fliqo.exception;

import java.util.function.Supplier;

public enum MemberSignupError implements Supplier<MemberSignupException> {
    EMAIL_ALREADY_EXISTS("이미 가입된 이메일입니다."),
    PASSWORD_POLICY_VIOLATION("비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다."),
    PASSWORD_MISMATCH("비밀번호와 비밀번호 확인이 일치하지 않습니다.");

    private final String message;

    MemberSignupError(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    @Override
    public MemberSignupException get() {
        return new MemberSignupException(this);
    }
}
