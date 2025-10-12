package com.fliqo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fliqo.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답 전용 DTO.
 * CommonResponse와 달리 Validation 에러처럼 필드별 상세 에러 정보가 필요한 경우 사용.
 * 주로 Bean Validation (@Valid) 실패 시 여러 필드의 에러를 한 번에 전달할 때 사용.
 *
 * @see CommonResponse
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean success,
        String message,
        String errorCode,
        LocalDateTime timestamp,
        List<FieldError> errors
) {

    /**
     * 컴팩트 생성자.
     * success를 항상 false로 고정.
     */
    public ErrorResponse {
        success = false;
    }

    /**
     * 필드별 에러 정보를 담는 내부 Record.
     * Bean Validation 실패 시 각 필드의 상세 에러 정보를 담음.
     *
     * @param field  에러가 발생한 필드명
     * @param value  입력된 잘못된 값
     * @param reason 에러 발생 이유
     */
    public record FieldError(
            String field,
            String value,
            String reason
    ) {}

    /**
     * 단순 에러 응답 생성 (필드별 상세 정보 없음).
     * Validation 에러가 아닌 일반적인 비즈니스 에러에 사용.
     *
     * @param message   사용자에게 보여줄 에러 메시지
     * @param errorCode 시스템 에러 코드
     * @return ErrorResponse 인스턴스
     */
    public static ErrorResponse of(String message, String errorCode) {
        return new ErrorResponse(false, message, errorCode, LocalDateTime.now(), null);
    }

    /**
     * ErrorCode enum을 사용한 에러 응답 생성.
     * 미리 정의된 표준 에러 코드를 사용할 때 편리.
     *
     * @param errorCode ErrorCode enum 값
     * @return ErrorResponse 인스턴스
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                false,
                errorCode.getMessage(),
                errorCode.getCode(),
                LocalDateTime.now(),
                null
        );
    }

    /**
     * Validation 에러 응답 생성 (필드별 상세 정보 포함).
     * Bean Validation (@Valid) 실패 시 사용.
     * 여러 필드의 에러를 한 번에 전달하여 프론트엔드에서 각 입력 필드 아래에 에러 메시지를 표시 가능.
     *
     * @param message   전체 에러 메시지
     * @param errorCode 에러 코드
     * @param errors    필드별 에러 상세 정보 리스트
     * @return ErrorResponse 인스턴스
     */
    public static ErrorResponse of(String message, String errorCode, List<FieldError> errors) {
        return new ErrorResponse(false, message, errorCode, LocalDateTime.now(), errors);
    }
}