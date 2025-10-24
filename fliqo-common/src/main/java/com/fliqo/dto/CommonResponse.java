package com.fliqo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fliqo.exception.ErrorCode;

/**
 * 모든 API 응답의 표준 포맷
 *
 * @param <T> 응답 데이터 타입
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 JSON에서 제외
public record CommonResponse<T>(
        boolean success, // 성공 여부 (true/false)
        T data, // 실제 데이터 (성공 시에만)
        String message, // 사용자 메시지 (선택)
        String errorCode, // 에러 코드 (실패 시에만)
        LocalDateTime timestamp // 응답 생성 시간
        ) {

    /** 성공 응답 - 데이터만 반환 사용: 조회, 생성, 수정 등 데이터가 있는 경우 */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, data, null, null, LocalDateTime.now());
    }

    /** 성공 응답 - 데이터 + 사용자 메시지 사용: 작업 완료 메시지를 사용자에게 보여줄 때 */
    public static <T> CommonResponse<T> success(T data, String message) {
        return new CommonResponse<>(true, data, message, null, LocalDateTime.now());
    }

    /** 성공 응답 - 메시지만 (데이터 없음) 사용: 삭제, 로그아웃 등 반환 데이터가 없는 경우 */
    public static <T> CommonResponse<T> successWithMessage(String message) {
        return new CommonResponse<>(true, null, message, null, LocalDateTime.now());
    }

    /** 실패 응답 - 커스텀 메시지 + 에러코드 사용: 동적 에러 메시지가 필요한 경우 */
    public static <T> CommonResponse<T> error(String message, String errorCode) {
        return new CommonResponse<>(false, null, message, errorCode, LocalDateTime.now());
    }

    /** 실패 응답 - ErrorCode enum 사용 사용: 표준화된 에러 코드/메시지를 사용하는 경우 */
    public static <T> CommonResponse<T> error(ErrorCode errorCode) {
        return new CommonResponse<>(
                false, null, errorCode.getMessage(), errorCode.getCode(), LocalDateTime.now());
    }
}
