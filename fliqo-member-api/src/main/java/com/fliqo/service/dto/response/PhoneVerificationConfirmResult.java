package com.fliqo.service.dto.response;

import lombok.Builder;

@Builder
public record PhoneVerificationConfirmResult(String verificationToken) {
    /**
     * 휴대폰 인증 코드 확인 결과를 표현하는 응답 모델을 생성합니다.
     *
     * <p>{@code verificationToken}은 이후 회원가입/로그인 흐름에서 휴대폰 인증 완료 상태를 증명하는 토큰으로 사용될 수 있습니다.
     *
     * @param verificationToken 인증 완료를 증명하는 토큰(Null 허용 안 함)
     * @return {@link PhoneVerificationConfirmResult} 인스턴스
     */
    public static PhoneVerificationConfirmResult of(String verificationToken) {
        return PhoneVerificationConfirmResult.builder()
                .verificationToken(verificationToken)
                .build();
    }
}
