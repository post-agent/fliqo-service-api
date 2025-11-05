package com.fliqo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fliqo.domain.entity.Member;
import com.fliqo.domain.entity.PhoneVerification;
import com.fliqo.domain.repository.MemberRepository;
import com.fliqo.service.dto.request.EmailFindStartCommand;
import com.fliqo.service.dto.request.EmailFindVerifyCommand;
import com.fliqo.service.dto.request.PhoneVerificationConfirmCommand;
import com.fliqo.service.dto.request.PhoneVerificationStartCommand;
import com.fliqo.service.dto.response.EmailFindResult;
import com.fliqo.service.dto.response.EmailFindStartResult;
import com.fliqo.service.dto.response.PhoneVerificationConfirmResult;
import com.fliqo.service.dto.response.PhoneVerificationStartResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailFindService {

    private final MemberRepository memberRepository;
    private final PhoneVerificationService phoneVerificationService;

    @Transactional
    public EmailFindStartResult start(EmailFindStartCommand emailFindStartCmd) {
        PhoneVerificationStartResult startResult =
                phoneVerificationService.start(
                        PhoneVerificationStartCommand.of(emailFindStartCmd.phoneNumber()));

        return EmailFindStartResult.of(startResult.verificationId(), startResult.expiresInMinutes());
    }

    @Transactional
    public EmailFindResult verify(EmailFindVerifyCommand emailFindVerifyCmd) {
        PhoneVerificationConfirmResult confirmResult =
                phoneVerificationService.confirm(
                        PhoneVerificationConfirmCommand.of(
                                emailFindVerifyCmd.verificationId(), emailFindVerifyCmd.code()));

        PhoneVerification phoneVerification =
                phoneVerificationService.getByTokenOfThrow(confirmResult.verificationToken());

        EmailFindResult result =
                memberRepository
                        .findByPhone(phoneVerification.getPhoneNumber())
                        .map(Member::getEmail)
                        .map(this::maskEmail)
                        .map(EmailFindResult::found)
                        .orElseGet(EmailFindResult::notFound);

        phoneVerificationService.consumeToken(phoneVerification);

        return result;
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (localPart.length() <= 2) {
            return localPart.substring(0, 1) + "***" + domainPart;
        }

        int visibleCount = Math.min(3, localPart.length());
        String visible = localPart.substring(0, visibleCount);
        String masked = "*".repeat(Math.max(1, localPart.length() - visibleCount));
        return visible + masked + domainPart;
    }
}
