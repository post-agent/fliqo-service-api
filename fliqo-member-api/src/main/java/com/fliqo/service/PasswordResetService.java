package com.fliqo.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fliqo.domain.entity.Member;
import com.fliqo.domain.entity.PasswordResetToken;
import com.fliqo.domain.entity.PhoneVerification;
import com.fliqo.domain.repository.MemberCredentialRepository;
import com.fliqo.domain.repository.MemberRepository;
import com.fliqo.domain.repository.PasswordResetTokenRepository;
import com.fliqo.service.dto.request.*;
import com.fliqo.service.dto.response.PasswordResetStartResult;
import com.fliqo.service.dto.response.PasswordResetVerifyResult;
import com.fliqo.service.dto.response.PhoneVerificationConfirmResult;
import com.fliqo.service.dto.response.PhoneVerificationStartResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final MemberRepository memberRepository;
    private final MemberCredentialRepository credentialRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PhoneVerificationService phoneVerificationService;
    private final PasswordEncoder passwordEncoder;

    private static final long RESET_TOKEN_TTL_SECONDS = 10 * 60;

    @Transactional
    public PasswordResetStartResult start(PasswordResetStartCommand passwordResetStartCmd) {
        var member =
                memberRepository
                        .findByEmail(passwordResetStartCmd.email())
                        .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));
        if (!member.getPhone().equals(passwordResetStartCmd.phoneNumber())) {
            throw new IllegalArgumentException("등록된 휴대전화와 일치하지 않습니다.");
        }

        PhoneVerificationStartResult result =
                phoneVerificationService.start(
                        PhoneVerificationStartCommand.of(passwordResetStartCmd.phoneNumber()));
        return PasswordResetStartResult.of(result.verificationId(), result.expiresInMinutes());
    }

    @Transactional
    public PasswordResetVerifyResult verify(PasswordResetVerifyCommand passwordResetVerifyCmd) {
        PhoneVerificationConfirmResult confirmResult =
                phoneVerificationService.confirm(
                        PhoneVerificationConfirmCommand.of(
                                passwordResetVerifyCmd.verificationId(),
                                passwordResetVerifyCmd.code()));

        PhoneVerification phoneVerification =
                phoneVerificationService.getByTokenOfThrow(confirmResult.verificationToken());

        Member member =
                memberRepository
                        .findByPhone(phoneVerification.getPhoneNumber())
                        .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        var resetToken = PasswordResetToken.issue(member.getId(), RESET_TOKEN_TTL_SECONDS);
        tokenRepository.save(resetToken);

        phoneVerificationService.consumeToken(phoneVerification);

        return PasswordResetVerifyResult.of(resetToken.getToken(), RESET_TOKEN_TTL_SECONDS);
    }

    @Transactional
    public void confirm(PasswordResetConfirmCommand passwordResetConfirmCmd) {
        var prt =
                tokenRepository
                        .findByToken(passwordResetConfirmCmd.resetToken())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 요청입니다."));

        if (!prt.isUsable(Instant.now())) {
            throw new IllegalArgumentException("만료되었거나 이미 사용된 토큰입니다.");
        }

        var credential =
                credentialRepository
                        .findByMemberId(prt.getMemberId())
                        .orElseThrow(() -> new IllegalArgumentException("자격 증명을 찾을 수 없습니다."));

        credential.changePassword(passwordEncoder.encode(passwordResetConfirmCmd.newPassword()));
    }
}
