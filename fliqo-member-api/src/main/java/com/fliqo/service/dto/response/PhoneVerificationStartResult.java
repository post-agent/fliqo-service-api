package com.fliqo.service.dto.response;

import lombok.Builder;

@Builder
public record PhoneVerificationStartResult(String verificationId, long expiresInMinutes) {
    /**
     * 휴대폰 인증 요청(코드 발송) 결과를 표현하는 응답 모델을 생성합니다.
     *
     * <p>반환된 {@code verificationId}는 인증 확인 단계에서 필수로 사용되며, {@code expiresInMinutes}는 해당 인증 세션의 유효
     * 시간입니다.
     *
     * @param verificationId 인증 세션을 식별하는 ID(Null 허용 안 함)
     * @param expiresInMinutes 세션 만료까지 남은 시간(분 단위, 0 이상)
     * @return {@link PhoneVerificationStartResult} 인스턴스
     */
    public static PhoneVerificationStartResult of(String verificationId, long expiresInMinutes) {
        return PhoneVerificationStartResult.builder()
                .verificationId(verificationId)
                .expiresInMinutes(expiresInMinutes)
                .build();
    }
}
