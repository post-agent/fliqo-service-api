package com.fliqo.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fliqo.config.JwtProperties;
import com.fliqo.controller.dto.response.TokenResponseDto;
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
    private final JwtProperties jwtProperties;

    @Transactional(readOnly = true)
    public EmailCheckResult checkEmail(EmailCheckCommand emailCheckCmd) {
        boolean exists = memberRepository.existsByEmail(emailCheckCmd.email());
        return new EmailCheckResult(exists);
    }

    /**
     * 회원가입을 처리합니다. 이메일 중복 검증, 전화번호 인증 확인, 회원 생성, 인증정보 저장 과정을 거칩니다.
     *
     * @param signupCmd 회원가입에 필요한 정보를 담은 커맨드 객체
     * @return 생성된 회원의 정보를 담은 SignupResult
     * @throws IllegalStateException 이메일이 이미 사용 중인 경우
     * @throws IllegalArgumentException 전화번호 인증이 유효하지 않은 경우
     */
    @Transactional
    public SignupResult signup(SignupCommand signupCmd) {
        validateSignupRequest(signupCmd);
        PhoneVerification phoneVerification = verifyPhone(signupCmd);
        Member member = createMember(signupCmd);
        createCredential(member, signupCmd.rawPassword());
        phoneVerificationService.consumeToken(phoneVerification);

        return buildSignupResult(member);
    }

    /**
     * 회원가입 요청의 유효성을 검증합니다. 이메일 중복 여부를 확인합니다.
     *
     * @param signupCmd 회원가입 커맨드 객체
     * @throws IllegalStateException 이메일이 이미 사용 중인 경우
     */
    private void validateSignupRequest(SignupCommand signupCmd) {
        memberPolicyValidator.ensureEmailAvailable(signupCmd.email());
    }

    /**
     * 전화번호 인증을 확인합니다. 인증 토큰을 조회하고 전화번호가 일치하는지 검증합니다.
     *
     * @param signupCmd 회원가입 커맨드 객체
     * @return 검증된 PhoneVerification 객체
     * @throws IllegalArgumentException 인증 토큰이 유효하지 않거나 전화번호가 일치하지 않는 경우
     */
    private PhoneVerification verifyPhone(SignupCommand signupCmd) {
        PhoneVerification phoneVerification =
                phoneVerificationService.getByTokenOfThrow(signupCmd.phoneVerificationToken());
        phoneVerification.assertPhoneMatches(signupCmd.phoneNumber());
        return phoneVerification;
    }

    /**
     * 새로운 회원 엔티티를 생성하고 저장합니다. UUID, 이메일, 이름, 전화번호로 회원 정보를 구성합니다.
     *
     * @param signupCmd 회원가입 커맨드 객체
     * @return 저장된 Member 엔티티
     */
    private Member createMember(SignupCommand signupCmd) {
        Member member =
                Member.builder()
                        .memberUuid(UuidUtil.newUuid())
                        .email(signupCmd.email())
                        .name(signupCmd.name())
                        .phone(signupCmd.phoneNumber())
                        .build();
        return memberRepository.save(member);
    }

    /**
     * 회원의 인증정보(비밀번호)를 생성하고 저장합니다. 평문 비밀번호를 암호화하여 MemberCredential 엔티티로 저장합니다.
     *
     * @param member 인증정보를 생성할 회원 엔티티
     * @param rawPassword 암호화되지 않은 평문 비밀번호
     */
    private void createCredential(Member member, String rawPassword) {
        String hash = passwordEncoder.encode(rawPassword);
        MemberCredential credential = MemberCredential.createNew(member, hash);
        credentialRepository.save(credential);
    }

    /**
     * 회원가입 결과 응답 객체를 생성합니다. 저장된 회원 정보를 바탕으로 SignupResult DTO를 구성합니다.
     *
     * @param member 생성된 회원 엔티티
     * @return 회원 정보를 담은 SignupResult 응답 객체
     */
    private SignupResult buildSignupResult(Member member) {
        return SignupResult.builder()
                .memberId(member.getId())
                .memberUuid(member.getMemberUuid())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhone())
                .build();
    }

    /**
     * 이메일/비밀번호를 검증한 뒤, JWT 토큰과 만료 시간을 포함한 응답을 생성합니다.
     *
     * @param email 로그인 이메일
     * @param rawPassword 평문 비밀번호
     * @return 액세스 토큰과 만료 시간을 담은 TokenResponseDto
     * @throws com.fliqo.exception.MemberException 자격 증명 검증 실패 시
     */
    @Transactional
    public TokenResponseDto login(String email, String rawPassword) {
        Member member = verifyCredential(email, rawPassword);
        String accessToken = generateAccessToken(member);
        long expirationSeconds = calculateExpirationSeconds();

        return TokenResponseDto.of(accessToken, expirationSeconds);
    }

    /**
     * 회원 정보를 기반으로 JWT 액세스 토큰을 생성합니다.
     *
     * @param member 인증된 회원 엔티티
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateAccessToken(Member member) {
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

    /**
     * 액세스 토큰의 만료 시간을 초 단위로 계산합니다.
     *
     * @return 만료 시간(초)
     */
    private long calculateExpirationSeconds() {
        return jwtProperties.getAccessMin() * 60;
    }
}
