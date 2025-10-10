package com.fliqo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 모든 커스텀 예외의 최상위 추상 클래스.
 * 시스템 내 모든 비즈니스 예외는 이 클래스를 상속받아야 함.
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    /**
     * ErrorCode enum을 사용한 예외 생성.
     *
     * @param errorCode ErrorCode enum 값
     */
    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.errorCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * ErrorCode enum을 사용하되 메시지를 커스터마이징.
     * 동적 메시지가 필요한 경우 사용.
     *
     * @param errorCode     ErrorCode enum 값
     * @param customMessage 커스텀 메시지
     */
    protected BaseException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.httpStatus = errorCode.getHttpStatus();
        this.errorCode = errorCode.getCode();
        this.message = customMessage;
    }

    /**
     * 직접 HTTP 상태 코드, 에러 코드, 메시지를 지정.
     * ErrorCode에 없는 예외를 만들 때 사용.
     *
     * @param httpStatus HTTP 상태 코드
     * @param errorCode  에러 코드
     * @param message    에러 메시지
     */
    protected BaseException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}