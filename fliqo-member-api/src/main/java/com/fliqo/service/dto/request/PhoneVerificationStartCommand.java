package com.fliqo.service.dto.request;

import com.fliqo.domain.entity.PhoneVerificationPurpose;

import lombok.Builder;

@Builder
public record PhoneVerificationStartCommand(String phoneNumber, PhoneVerificationPurpose purpose) {
    /**
     * 휴대폰 인증 요청(코드 발송)을 시작하기 위한 서비스 레이어 명령 객체를 생성합니다.
     *
     * <p>{@code phoneNumber, @code purpose}는 서비스에서 요구하는 형식으로 전달되어야 합니다.
     *
     * @param phoneNumber 인증 대상 휴대전화 번호(Null 허용 안 함)
     * @param purpose 휴대폰 인증 목적(Null 허용 안 함)
     * @return {@link PhoneVerificationStartCommand} 인스턴스
     */
    public static PhoneVerificationStartCommand of(
            String phoneNumber, PhoneVerificationPurpose purpose) {

        return PhoneVerificationStartCommand.builder()
                .phoneNumber(phoneNumber)
                .purpose(purpose)
                .build();
    }
}
