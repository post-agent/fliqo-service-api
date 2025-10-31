package com.fliqo.service.validator;

import org.springframework.stereotype.Component;

import com.fliqo.domain.entity.PhoneVerification;
import com.fliqo.domain.repository.PhoneVerificationRepository;
import com.fliqo.exception.BadRequestException;
import com.fliqo.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhoneVerifyValidator {
    private final PhoneVerificationRepository repository;

    /**
     * 인증 세션 ID로 엔터티를 조회하고, 없으면 {@code PHONE_VERIFY_INVALID_REQUEST} 예외를 발생시킵니다.
     *
     * @param verificationId 인증 세션 ID
     * @return 조회된 {@link PhoneVerification}
     * @throws com.fliqo.exception.BadRequestException 세션이 존재하지 않는 경우
     */
    public PhoneVerification mustExist(String verificationId) {
        return repository
                .findById(verificationId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.PHONE_VERIFY_INVALID_REQUEST));
    }

    /**
     * 검증 완료 토큰으로 엔터티를 조회하고, 없거나 미완료 상태면 {@code PHONE_VERIFY_NOT_COMPLETED} 예외를 발생시킵니다.
     *
     * @param token 검증 완료 토큰
     * @return 조회된 {@link PhoneVerification}
     * @throws com.fliqo.exception.BadRequestException 검증 미완료 또는 토큰 불일치인 경우
     */
    public PhoneVerification mustBeCompletedToken(String token) {
        return repository
                .findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException(ErrorCode.PHONE_VERIFY_NOT_COMPLETED));
    }
}
