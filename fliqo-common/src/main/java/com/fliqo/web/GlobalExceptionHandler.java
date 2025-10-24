package com.fliqo.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fliqo.dto.CommonResponse;
import com.fliqo.dto.ErrorResponse;
import com.fliqo.exception.BaseException;
import com.fliqo.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

/** 전역 예외 처리 핸들러. 모든 Controller에서 발생하는 예외를 한 곳에서 처리하여 일관된 에러 응답 제공. */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(jakarta.servlet.ServletException.class)
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외 처리 (BaseException 계열). 시스템에서 의도적으로 발생시킨 모든 비즈니스 예외를 처리.
     *
     * @param e BaseException 또는 하위 예외
     * @return 에러 응답
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponse<Void>> handleBaseException(BaseException e) {
        log.error("BaseException: [{}] {}", e.getErrorCode(), e.getMessage(), e);

        return ResponseEntity.status(e.getHttpStatus())
                .body(CommonResponse.error(e.getMessage(), e.getErrorCode()));
    }

    /**
     * Bean Validation 예외 처리 (@Valid 실패). DTO 필드 검증 실패 시 각 필드별 에러 정보를 포함한 상세 응답 반환.
     *
     * @param e MethodArgumentNotValidException
     * @return 필드별 에러 정보를 포함한 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {

        List<ErrorResponse.FieldError> errors =
                e.getBindingResult().getAllErrors().stream()
                        .map(
                                error -> {
                                    FieldError fieldError = (FieldError) error;
                                    return new ErrorResponse.FieldError(
                                            fieldError.getField(),
                                            fieldError.getRejectedValue() != null
                                                    ? fieldError.getRejectedValue().toString()
                                                    : "",
                                            fieldError.getDefaultMessage());
                                })
                        .collect(Collectors.toList());

        log.warn("Validation failed: {}", errors);

        ErrorResponse response =
                ErrorResponse.of(
                        "입력값이 올바르지 않습니다.", ErrorCode.INVALID_INPUT_VALUE.getCode(), errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 필수 파라미터 누락 예외 처리. @RequestParam(required=true) 파라미터가 없을 때 발생.
     *
     * @param e MissingServletRequestParameterException
     * @return 에러 응답
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResponse<Void>> handleMissingParameter(
            MissingServletRequestParameterException e) {

        log.warn("Missing parameter: {}", e.getParameterName());

        String message = String.format("필수 파라미터가 누락되었습니다: %s", e.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(message, ErrorCode.MISSING_REQUEST_PARAMETER.getCode()));
    }

    /**
     * 타입 불일치 예외 처리. 파라미터 타입이 맞지 않을 때 발생 (예: String을 Integer로 변환 실패).
     *
     * @param e MethodArgumentTypeMismatchException
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e) {

        log.warn("Type mismatch: {} - expected {}", e.getName(), e.getRequiredType());

        String message = String.format("'%s' 파라미터의 타입이 올바르지 않습니다.", e.getName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(message, ErrorCode.INVALID_TYPE_VALUE.getCode()));
    }

    /**
     * HTTP 메서드 불일치 예외 처리. 지원하지 않는 HTTP 메서드로 요청했을 때 발생 (예: POST 엔드포인트에 GET 요청).
     *
     * @param e HttpRequestMethodNotSupportedException
     * @return 에러 응답
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e) {

        log.warn("Method not supported: {}", e.getMethod());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(CommonResponse.error(ErrorCode.METHOD_NOT_ALLOWED));
    }

    /**
     * JSON 파싱 에러 처리. 요청 본문(Body)의 JSON 형식이 올바르지 않을 때 발생.
     *
     * @param e HttpMessageNotReadableException
     * @return 에러 응답
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {

        log.error("JSON parse error: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error("요청 본문을 읽을 수 없습니다.", "INVALID_JSON"));
    }

    /**
     * 그 외 모든 예외 처리. 예상하지 못한 서버 오류 처리 (500 Internal Server Error).
     *
     * @param e Exception
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
