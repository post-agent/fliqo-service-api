package com.fliqo.service;

import java.security.SecureRandom;

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
     * @param cmd 휴대폰 번호를 담은 시작 명령
     * @return 인증 세션 ID와 만료 정보를 담은 결과 객체
     */
    @Transactional
    public PhoneVerificationStartResult start(PhoneVerificationStartCommand cmd) {
        // 인증 코드 생성 및 엔터티 생성
        String code = generateCode();
        PhoneVerification pv = PhoneVerification.createNew(cmd.phoneNumber(), code);

        // 저장
        repository.save(pv);

        // SMS 발송
        smsSender.send(cmd.phoneNumber(), "[Fliqo] 인증번호: " + code + " (유효시간 3분)");

        return PhoneVerificationStartResult.of(pv.getId(), pv.expiresInSeconds());
    }

    /**
     * 사용자가 제출한 인증 코드를 검증하고 검증 토큰을 발급합니다.
     *
     * <p>세션 존재 여부 및 상태 검증은 {@link PhoneVerifyValidator}가 수행하며, 코드 일치/만료/시도 횟수 검증은 엔터티의 도메인
     * 로직({@link PhoneVerification#verify(String)})이 수행합니다.
     *
     * @param cmd 인증 세션 ID와 사용자 입력 코드를 담은 확인 명령
     * @return 검증 토큰을 담은 결과 객체
     */
    @Transactional
    public PhoneVerificationConfirmResult confirm(PhoneVerificationConfirmCommand cmd) {
        PhoneVerification pv = validator.mustExist(cmd.verificationId());
        pv.verify(cmd.code());
        repository.save(pv);

        return PhoneVerificationConfirmResult.of(pv.getVerificationToken());
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
     * @param pv 소모 처리할 인증 엔터티
     */
    @Transactional
    public void consumeToken(PhoneVerification pv) {
        pv.consume();
        repository.save(pv);
    }
}
