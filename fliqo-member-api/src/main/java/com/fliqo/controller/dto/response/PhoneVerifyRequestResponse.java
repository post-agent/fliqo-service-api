package com.fliqo.controller.dto.response;

import lombok.Builder;

@Builder
public record PhoneVerifyRequestResponse(String verificationId, long expiresInMinutes) {
    /**
     * 휴대폰 인증 요청(코드 발송) 응답 객체를 생성합니다.
     *
     * <p>클라이언트는 반환된 {@code verificationId}를 보관했다가 이후 인증 코드 확인 단계에서 함께 전송해야 합니다.
     *
     * @param verificationId 인증 세션을 식별하는 ID(Null 허용 안 함)
     * @param expiresInMinutes 인증 세션 만료까지 남은 시간(분 단위, 0 이상)
     * @return {@link PhoneVerifyRequestResponse} 인스턴스
     */
    public static PhoneVerifyRequestResponse of(String verificationId, long expiresInMinutes) {
        return PhoneVerifyRequestResponse.builder()
                .verificationId(verificationId)
                .expiresInMinutes(expiresInMinutes)
                .build();
    }
}
