package com.fliqo.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fliqo.domain.entity.Member;
import com.fliqo.domain.entity.MemberCredential;
import com.fliqo.domain.entity.PhoneVerification;
import com.fliqo.domain.repository.MemberCredentialRepository;
import com.fliqo.domain.repository.MemberRepository;
import com.fliqo.exception.MemberError;
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
    private final JwtService jwtService;

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

    /**
     * 이메일/비밀번호를 검증한 뒤, 사용자의 역할을 포함한 JWT를 발급합니다.
     *
     * @param email 로그인 이메일
     * @param rawPassword 평문 비밀번호
     * @return 생성된 JWT 토큰 문자열
     * @throws com.fliqo.exception.MemberException 자격 증명 검증 실패 시(내부적으로 {@link
     *     com.fliqo.exception.MemberError} 사용)
     */
    @Transactional
    public String login(String email, String rawPassword) {
        Member member = verifyCredential(email, rawPassword);

        List<String> roles = List.of(member.getRole().name());
        return jwtService.createToken(member.getEmail(), roles);
    }

    /**
     * 로그인에 필요한 자격 증명(이메일 존재, 비밀번호 일치)을 검증합니다.
     *
     * <p>다음 경우 {@link com.fliqo.exception.MemberException}을 던집니다:
     *
     * <ul>
     *   <li>{@link com.fliqo.exception.MemberError#MEMBER_NOT_FOUND} - 이메일로 회원 없음
     *   <li>{@link com.fliqo.exception.MemberError#CREDENTIAL_NOT_FOUND} - 자격 증명 없음
     *   <li>{@link com.fliqo.exception.MemberError#INVALID_PASSWORD} - 비밀번호 불일치
     * </ul>
     *
     * 검증 성공 시 {@link Member}를 반환합니다.
     *
     * @param email 로그인 이메일
     * @param rawPassword 평문 비밀번호
     * @return 검증된 {@link Member}
     * @throws com.fliqo.exception.MemberException 위의 경우들
     */
    private Member verifyCredential(String email, String rawPassword) {
        Member member =
                memberRepository.findByEmail(email).orElseThrow(MemberError.MEMBER_NOT_FOUND);

        MemberCredential memberCredential =
                credentialRepository
                        .findByMemberId(member.getId())
                        .orElseThrow(MemberError.CREDENTIAL_NOT_FOUND);

        if (!passwordEncoder.matches(rawPassword, memberCredential.getPasswordHash())) {
            throw MemberError.INVALID_PASSWORD.get();
        }

        return member;
    }
}
