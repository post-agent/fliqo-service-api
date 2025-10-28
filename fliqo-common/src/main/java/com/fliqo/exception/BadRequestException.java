package com.fliqo.exception;

import org.springframework.http.HttpStatus;

/** 잘못된 요청 예외 (400 Bad Request). 클라이언트의 요청이 잘못되었거나 유효하지 않을 때 발생. */
public class BadRequestException extends BaseException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }
}
