package com.fliqo.exception;

/** 인증 실패 예외 (401 Unauthorized). 로그인이 필요하거나 토큰이 유효하지 않을 때 발생. */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
