package com.fliqo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true).code(code).message(message).data(data).build();
    }

    public static ApiResponse<Void> error(String code, String message) {
        return ApiResponse.<Void>builder()
                .success(false).code(code).message(message).data(null).build();
    }
}
