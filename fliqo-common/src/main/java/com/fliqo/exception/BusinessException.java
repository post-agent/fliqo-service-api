package com.fliqo.exception;

import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 실행 중 발생하는 일반적인 예외.
 * 특정 카테고리에 속하지 않는 비즈니스 예외에 사용.
 */
public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public BusinessException(HttpStatus httpStatus, String errorCode, String message) {
        super(httpStatus, errorCode, message);
    }
}