package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record PhoneVerificationConfirmCommand(String verificationId, String code) {
    /**
     * 휴대폰 인증 코드 확인을 위한 서비스 레이어 명령 객체를 생성합니다.
     *
     * <p>{@code verificationId}는 인증 요청 단계에서 발급된 세션 ID이며, {@code code}는 사용자 휴대폰으로 전송된 1회용 인증 코드입니다.
     *
     * @param verificationId 인증 세션 ID(Null 허용 안 함)
     * @param code 문자로 수신한 인증 코드(Null 허용 안 함)
     * @return {@link PhoneVerificationConfirmCommand} 인스턴스
     */
    public static PhoneVerificationConfirmCommand of(String verificationId, String code) {
        return PhoneVerificationConfirmCommand.builder()
                .verificationId(verificationId)
                .code(code)
                .build();
    }
}
