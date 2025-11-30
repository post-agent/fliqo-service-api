package com.fliqo.domain.entity;

import java.util.Arrays;

public enum PhoneVerificationPurpose {
    SIGNUP(0),
    FIND_EMAIL(1),
    PASSWORD_RESET(2);

    private final int code;

    PhoneVerificationPurpose(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PhoneVerificationPurpose fromCode(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code : " + code));
    }
}
