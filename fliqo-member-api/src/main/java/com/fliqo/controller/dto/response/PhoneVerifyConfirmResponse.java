package com.fliqo.controller.dto.response;

import lombok.Builder;

@Builder
public record PhoneVerifyConfirmResponse(String phoneVerificationToken) {
    /**
     * 휴대폰 인증 확인 응답 객체를 생성합니다.
     *
     * <p>인증 코드 검증이 성공하면 서버가 발급한 {@code phoneVerificationToken}을 클라이언트에 전달하는 용도로 사용됩니다.
     *
     * @param phoneVerificationToken 인증 확인에 성공했음을 증명하는 토큰(Null 허용 안 함)
     * @return {@link PhoneVerifyConfirmResponse} 인스턴스
     */
    public static PhoneVerifyConfirmResponse of(String phoneVerificationToken) {
        return PhoneVerifyConfirmResponse.builder()
                .phoneVerificationToken(phoneVerificationToken)
                .build();
    }
}
