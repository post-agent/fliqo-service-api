package com.fliqo.exception;

import org.springframework.http.HttpStatus;

/** 리소스를 찾을 수 없을 때 발생하는 예외 (404 Not Found). 회원, 매장, 예약 등의 엔티티 조회 실패 시 사용. */
public class NotFoundException extends BaseException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
}
