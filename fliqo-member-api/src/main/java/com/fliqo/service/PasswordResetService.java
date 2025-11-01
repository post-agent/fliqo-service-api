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
import com.fliqo.exception.BadRequestException;
import com.fliqo.exception.ErrorCode;
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
                        .orElseThrow(
                                () -> new BadRequestException(ErrorCode.RESET_INVALID_REQUEST));
        if (!member.getPhone().equals(passwordResetStartCmd.phoneNumber())) {
            throw new BadRequestException(ErrorCode.RESET_INVALID_REQUEST);
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
                        .orElseThrow(
                                () -> new BadRequestException(ErrorCode.RESET_INVALID_REQUEST));

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
                        .orElseThrow(() -> new BadRequestException(ErrorCode.RESET_TOKEN_INVALID));

        if (!prt.isUsable(Instant.now())) {
            throw new BadRequestException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        var credential =
                credentialRepository
                        .findByMemberId(prt.getMemberId())
                        .orElseThrow(
                                () -> new BadRequestException(ErrorCode.RESET_INVALID_REQUEST));

        credential.changePassword(passwordEncoder.encode(passwordResetConfirmCmd.newPassword()));
    }
}
