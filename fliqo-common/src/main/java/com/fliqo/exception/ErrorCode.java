package com.fliqo.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 시스템 전체에서 사용하는 표준 에러 코드. 필요에 따라 점진적으로 추가하여 사용. */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== Common (공통) =====
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_003", "지원하지 않는 HTTP 메서드입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_004", "요청 값의 타입이 올바르지 않습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_005", "필수 파라미터가 누락되었습니다."),

    // ===== Authentication & Authorization (인증 및 권한) =====
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_002", "접근 권한이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_003", "이메일 또는 비밀번호가 올바르지 않습니다."),

    // ===== Resource Not Found (리소스 없음) =====
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_001", "요청한 리소스를 찾을 수 없습니다."),

    // ===== Member (회원) =====
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER_001", "이미 가입된 이메일입니다."),
    PASSWORD_POLICY_VIOLATION(
            HttpStatus.BAD_REQUEST, "MEMBER_002", "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER_003", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_004", "회원을 찾을 수 없습니다."),

    // ===== Phone Verification (전화번호 인증) =====
    PHONE_VERIFY_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "PHONE_001", "유효하지 않은 인증 요청입니다."),
    PHONE_VERIFY_EXPIRED(HttpStatus.BAD_REQUEST, "PHONE_002", "인증 코드가 만료되었습니다."),
    PHONE_VERIFY_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "PHONE_003", "이미 처리된 인증 요청입니다."),
    PHONE_VERIFY_ATTEMPTS_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "PHONE_004", "인증 시도 횟수를 초과했습니다."),
    PHONE_VERIFY_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "PHONE_005", "인증 코드가 올바르지 않습니다."),
    PHONE_VERIFY_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "PHONE_006", "휴대폰 인증이 완료되지 않았습니다."),
    PHONE_VERIFY_PHONE_MISMATCH(
            HttpStatus.BAD_REQUEST, "PHONE_007", "인증된 휴대폰 번호와 제출된 번호가 일치하지 않습니다."),

    // ===== password reset =====
    RESET_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "RESET_001", "회원 정보를 찾을 수 없습니다."),
    RESET_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "RESET_002", "비밀번호 재설정 토큰이 유효하지 않습니다."),
    RESET_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "RESET_003", "비밀번호 재설정 토큰이 만료되었거나 이미 사용되었습니다."),

    AUTH_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "", ""),
    AUTH_USER_INFO_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "", ""),
    AUTH_PROVIDER_NOT_CONFIGURED(HttpStatus.BAD_REQUEST, "", ""),
    AUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "", "");
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
