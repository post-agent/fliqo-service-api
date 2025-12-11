package com.fliqo.service;

import java.security.SecureRandom;

import com.fliqo.domain.entity.PhoneVerificationPurpose;
import com.fliqo.service.validator.MemberPolicyValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fliqo.domain.entity.PhoneVerification;
import com.fliqo.domain.repository.PhoneVerificationRepository;
import com.fliqo.service.dto.request.PhoneVerificationConfirmCommand;
import com.fliqo.service.dto.request.PhoneVerificationStartCommand;
import com.fliqo.service.dto.response.PhoneVerificationConfirmResult;
import com.fliqo.service.dto.response.PhoneVerificationStartResult;
import com.fliqo.service.validator.PhoneVerifyValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private final PhoneVerificationRepository repository;
    private final SmsSender smsSender;
    private final PhoneVerifyValidator validator;
    private final MemberPolicyValidator memberPolicyValidator;

    private static final int MAX_ATTEMPTS = 5;

    // 6자리 인증 코드 생성
    private String generateCode() {
        SecureRandom r = new SecureRandom();
        int n = r.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    /**
     * 휴대폰 인증을 시작하고 인증 코드를 발송합니다.
     *
     * <p>인증 세션을 생성하여 저장한 뒤, 대상 번호로 일회용 인증 코드를 SMS로 전송합니다.
     *
     * @param phoneVerificationStartCmd 휴대폰 번호를 담은 시작 명령
     * @return 인증 세션 ID와 만료 정보를 담은 결과 객체
     */
    @Transactional
    public PhoneVerificationStartResult start(
            PhoneVerificationStartCommand phoneVerificationStartCmd) {

        String phoneNumber = phoneVerificationStartCmd.phoneNumber();
        PhoneVerificationPurpose purpose = phoneVerificationStartCmd.purpose();

        validatePhonePolicyByPurpose(phoneNumber, purpose);

        // 인증 코드 생성 및 엔터티 생성
        String code = generateCode();

        PhoneVerification phoneVerification =
                PhoneVerification.createNew(
                        phoneVerificationStartCmd.phoneNumber(),
                        code,
                        phoneVerificationStartCmd.purpose());

        // 저장
        repository.save(phoneVerification);

        // SMS 발송
        smsSender.send(
                phoneVerificationStartCmd.phoneNumber(), "[Fliqo] 인증번호: " + code + " (유효시간 3분)");

        return PhoneVerificationStartResult.of(
                phoneVerification.getId(), phoneVerification.expiresInSeconds());
    }

    /**
     * 사용자가 제출한 인증 코드를 검증하고 검증 토큰을 발급합니다.
     *
     * <p>세션 존재 여부 및 상태 검증은 {@link PhoneVerifyValidator}가 수행하며, 코드 일치/만료/시도 횟수 검증은 엔터티의 도메인
     * 로직({@link PhoneVerification#verify(String)})이 수행합니다.
     *
     * @param phoneVerificationConfirmCommand 인증 세션 ID와 사용자 입력 코드를 담은 확인 명령
     * @return 검증 토큰을 담은 결과 객체
     */
    @Transactional
    public PhoneVerificationConfirmResult confirm(
            PhoneVerificationConfirmCommand phoneVerificationConfirmCommand) {
        PhoneVerification phoneVerification = verifyAndGet(phoneVerificationConfirmCommand);

        return PhoneVerificationConfirmResult.of(phoneVerification.getVerificationToken());
    }

    /**
     * 인증 코드를 검증하고 {@link PhoneVerification} 엔터티를 반환합니다.
     *
     * <p>주요 흐름은 {@link #confirm(PhoneVerificationConfirmCommand)}와 동일하며, 검증이 성공하면 엔터티를 그대로 돌려줍니다.
     * 이메일 찾기 등 인증 이후 추가 정보가 필요한 시나리오에서 재사용하기 위한 유틸리티입니다.
     *
     * @param phoneVerificationConfirmCommand 인증 세션 ID와 사용자 입력 코드를 담은 확인 명령
     * @return 검증이 완료된 {@link PhoneVerification}
     */
    @Transactional
    public PhoneVerification verifyAndGet(
            PhoneVerificationConfirmCommand phoneVerificationConfirmCommand) {
        PhoneVerification phoneVerification =
                validator.mustExist(phoneVerificationConfirmCommand.verificationId());
        phoneVerification.verify(phoneVerificationConfirmCommand.code());
        repository.save(phoneVerification);

        return phoneVerification;
    }

    /**
     * 검증 완료 토큰으로 인증 정보를 조회합니다.
     *
     * <p>완료 여부/유효성 검증은 {@link PhoneVerifyValidator}가 담당합니다.
     *
     * @param token 검증 완료를 증명하는 토큰
     * @return 토큰에 해당하는 {@link PhoneVerification} 엔터티
     */
    @Transactional
    public PhoneVerification getByTokenOfThrow(String token) {
        return validator.mustBeCompletedToken(token);
    }

    /**
     * 검증 토큰을 소모 상태로 변경하여 재사용을 방지합니다.
     *
     * @param phoneVerification 소모 처리할 인증 엔터티
     */
    @Transactional
    public void consumeToken(PhoneVerification phoneVerification) {
        phoneVerification.consume();
        repository.save(phoneVerification);
    }
    /**
     * 인증 목적에 따라 휴대폰 번호의 사용 가능 여부 및 가입 여부를 검증합니다.
     *
     * <ul>
     *     <li>SIGNUP: 이미 가입된 번호면 예외</li>
     *     <li>PASSWORD_RESET, FIND_EMAIL: 가입된 번호가 아니면 예외</li>
     * </ul>
     */
    private void validatePhonePolicyByPurpose(String phoneNumber, PhoneVerificationPurpose purpose) {
        switch (purpose) {
            case SIGNUP -> {
                memberPolicyValidator.ensurePhoneAvailableForSignup(phoneNumber);
            }
            case FIND_EMAIL, PASSWORD_RESET -> {
                memberPolicyValidator.ensurePhoneAlreadyRegistered(phoneNumber);
            }
        }
    }
}
