package com.fliqo.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.fliqo.exception.PhoneVerifyError;
import com.fliqo.util.UuidUtil;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_phone_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerification {

    private static final int DEFAULT_CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;
    private static final int DEFAULT_EXPIRY_MINUTES = 3;

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = DEFAULT_CODE_LENGTH)
    private String code;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PhoneStatus status = PhoneStatus.PENDING;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verification_token", length = 36)
    private String verificationToken;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    /**
     * 새로운 휴대폰 인증 엔티티를 생성합니다.
     *
     * <p>- 인증 상태는 {@link PhoneStatus#PENDING} 으로 초기화됩니다.<br>
     * - 만료 시간은 현재 시각 기준 {@value DEFAULT_EXPIRY_MINUTES}분 후로 설정됩니다.<br>
     * - 시도 횟수는 0으로 초기화됩니다.
     *
     * @param phoneNumber 인증 대상 휴대폰 번호
     * @param code 발송할 인증 코드(6자리)
     * @return 생성된 {@link PhoneVerification} 엔티티
     */
    public static PhoneVerification createNew(String phoneNumber, String code) {
        LocalDateTime now = LocalDateTime.now();
        return PhoneVerification.builder()
                .id(UuidUtil.newUuid())
                .phoneNumber(phoneNumber)
                .code(code)
                .expiresAt(now.plusMinutes(DEFAULT_EXPIRY_MINUTES))
                .attempts(0)
                .build();
    }

    /**
     * 인증이 만료되었는지 여부를 반환합니다.
     *
     * @return 현재 시각이 {@code expiresAt} 이후라면 {@code true}, 아니면 {@code false}
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 현재 인증 상태가 대기(PENDING)인지 여부를 반환합니다.
     *
     * @return 상태가 {@link PhoneStatus#PENDING}이면 {@code true}, 아니면 {@code false}
     */
    public boolean isPending() {
        return PhoneStatus.PENDING.equals(status);
    }

    /**
     * 남은 유효 시간을 초 단위로 반환합니다.
     *
     * <p>참고: 현재 구현은 기본 만료 시간({@value DEFAULT_EXPIRY_MINUTES}분)을 초 단위로 고정 반환합니다.
     *
     * @return 남은 유효 시간(초)
     */
    public long expiresInSeconds() {
        return DEFAULT_EXPIRY_MINUTES * 60L;
    }

    /** 인증 상태를 만료(EXPIRED)로 변경합니다. */
    public void markExpired() {
        this.status = PhoneStatus.EXPIRED;
    }

    /**
     * 인증 상태를 사용됨(CONSUMED)으로 변경합니다.
     *
     * <p>일반적으로 인증 토큰을 실제 회원가입/변경 처리 등에서 사용 완료한 시점에 호출합니다.
     */
    public void consume() {
        this.status = PhoneStatus.CONSUMED;
    }

    /**
     * 입력된 코드로 인증을 수행합니다.
     *
     * <p>절차:
     *
     * <ol>
     *   <li>만료 여부 확인 후 만료 시 EXPIRED로 전이하고 예외 발생
     *   <li>PENDING 상태 여부 확인
     *   <li>시도 횟수 초과 시 EXPIRED 전이 및 예외 발생
     *   <li>시도 횟수 1 증가
     *   <li>코드 불일치 시 예외 발생
     *   <li>성공 시 VERIFIED 전이, 검증 시각/검증 토큰 설정
     * </ol>
     *
     * @param inputCode 사용자가 제출한 인증 코드
     * @throws RuntimeException 만료, 상태 부적합, 시도 횟수 초과, 코드 불일치 등 {@link PhoneVerifyError}에 대응하는 런타임
     *     예외가 발생합니다.
     */
    public void verify(String inputCode) {
        if (isExpired()) {
            markExpired();
            throw PhoneVerifyError.EXPIRED.get();
        }
        if (!isPending()) {
            throw PhoneVerifyError.ALREADY_PROCESSED.get();
        }
        if (this.attempts >= MAX_ATTEMPTS) {
            markExpired();
            throw PhoneVerifyError.ATTEMPTS_EXCEEDED.get();
        }

        this.attempts += 1;

        if (!Objects.equals(this.code, inputCode)) {
            throw PhoneVerifyError.CODE_MISMATCH.get();
        }

        this.status = PhoneStatus.VERIFIED;
        this.verifiedAt = LocalDateTime.now();
        this.verificationToken = UUID.randomUUID().toString();
    }

    /** 제출 번호가 이 인증 세션의 저장 번호와 일치하는지 확인합니다. 일치하지 않으면 PHONE_MISMATCH 예외를 던집니다. */
    public void assertPhoneMatches(String submittedPhoneNumber) {
        if (!Objects.equals(this.phoneNumber, submittedPhoneNumber)) {
            throw PhoneVerifyError.PHONE_MISMATCH.get();
        }
    }
}
