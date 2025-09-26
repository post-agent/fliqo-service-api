package com.fliqo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fliqo.domain.entity.Member;
import com.fliqo.domain.entity.MemberCredential;
import com.fliqo.domain.entity.PhoneVerification;
import com.fliqo.domain.repository.MemberCredentialRepository;
import com.fliqo.domain.repository.MemberRepository;
import com.fliqo.service.dto.request.EmailCheckCommand;
import com.fliqo.service.dto.request.SignupCommand;
import com.fliqo.service.dto.response.EmailCheckResult;
import com.fliqo.service.dto.response.SignupResult;
import com.fliqo.service.validator.MemberPolicyValidator;
import com.fliqo.util.UuidUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhoneVerificationService phoneVerificationService;
    private final MemberPolicyValidator memberPolicyValidator;

    @Transactional(readOnly = true)
    public EmailCheckResult checkEmail(EmailCheckCommand cmd) {
        boolean exists = memberRepository.existsByEmail(cmd.email());
        return new EmailCheckResult(exists);
    }

    @Transactional
    public SignupResult signup(SignupCommand cmd) {
        memberPolicyValidator.ensureEmailAvailable(cmd.email());

        PhoneVerification pv =
                phoneVerificationService.getByTokenOfThrow(cmd.phoneVerificationToken());
        pv.assertPhoneMatches(cmd.phoneNumber());

        Member member =
                Member.builder()
                        .memberUuid(UuidUtil.newUuid())
                        .email(cmd.email())
                        .name(cmd.name())
                        .phone(cmd.phoneNumber())
                        .build();
        memberRepository.save(member);

        String hash = passwordEncoder.encode(cmd.rawPassword());
        MemberCredential cred = MemberCredential.createNew(member, hash);
        credentialRepository.save(cred);

        phoneVerificationService.consumeToken(pv);

        return SignupResult.builder()
                .memberId(member.getId())
                .memberUuid(member.getMemberUuid())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhone())
                .build();
    }
}
