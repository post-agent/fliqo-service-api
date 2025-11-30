package com.fliqo.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fliqo.config.AuthProperties;
import com.fliqo.controller.dto.response.TokenResponseDto;
import com.fliqo.domain.entity.AuthProvider;
import com.fliqo.domain.entity.Member;
import com.fliqo.domain.repository.MemberRepository;
import com.fliqo.exception.BadRequestException;
import com.fliqo.exception.ErrorCode;
import com.fliqo.service.auth.*;
import com.fliqo.util.UuidUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AuthProperties authProperties;

    /**
     * 소셜 로그인 처리 1. Code로 Access Token 발급 2. Access Token으로 사용자 정보 조회 3. 회원 존재 여부 확인 후 생성/조회 4. JWT
     * 토큰 발급
     *
     * @param provider 소셜 제공자
     * @param code 인증 코드
     * @param redirectUri 콜백 URI
     * @return JWT 토큰 응답
     */
    @Transactional
    public TokenResponseDto login(AuthProvider provider, String code, String redirectUri) {
        // 1. Access Token 발급
        String accessToken = authClient.getAccessToken(provider, code, redirectUri);

        // 2. 사용자 정보 조회
        Map<String, Object> userAttributes = authClient.getUserInfo(provider, accessToken);
        AuthUserInfo userInfo = createUserInfo(provider, userAttributes);

        // 3. 회원 조회 또는 생성
        Member member = getOrCreateMember(userInfo);

        // 4. JWT 발급 (기존 MemberService 로직 재사용)
        String jwtToken = memberService.generateAccessToken(member);
        long expirationSeconds = calculateExpirationSeconds();

        log.info(
                "Social login success: provider={}, providerId={}, email={}",
                provider,
                userInfo.getProviderId(),
                userInfo.getEmail());

        return TokenResponseDto.of(jwtToken, expirationSeconds);
    }

    /** 소셜 제공자에 따라 적절한 UserInfo 객체 생성 */
    private AuthUserInfo createUserInfo(AuthProvider provider, Map<String, Object> attributes) {
        return provider.createUserInfo(attributes);
    }

    /** 기존 회원 조회 또는 신규 회원 생성 provider + providerId로 조회하고, 없으면 새로 생성 */
    private Member getOrCreateMember(AuthUserInfo userInfo) {
        Optional<Member> existingMember =
                memberRepository.findByProviderAndProviderId(
                        userInfo.getProvider(), userInfo.getProviderId());

        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            member.markLastLoginNow();
            return member;
        }

        // 신규 회원 생성
        return createNewMember(userInfo);
    }

    /** 소셜 로그인으로 신규 회원 생성 */
    private Member createNewMember(AuthUserInfo userInfo) {
        String email = userInfo.getEmail();

        // 이메일 중복 체크 (다른 제공자로 이미 가입한 경우)
        if (memberRepository.existsByEmail(email)) {
            throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // name이 없으면 nickname을, nickname도 없으면 이메일 앞부분을 사용
        String name = userInfo.getName();
        if (name == null || name.isBlank()) {
            name = userInfo.getNickname();
            if (name == null || name.isBlank()) {
                name = email.split("@")[0];
            }
        }

        Member member =
                Member.builder()
                        .memberUuid(UuidUtil.newUuid())
                        .email(email)
                        .name(name)
                        .nickname(userInfo.getNickname())
                        .phone(userInfo.getPhone())
                        .provider(userInfo.getProvider())
                        .providerId(userInfo.getProviderId())
                        .profileImageUrl(userInfo.getProfileImageUrl())
                        .emailVerified(userInfo.getEmail() != null) // 이메일이 있을 때만 인증 완료
                        .build();

        return memberRepository.save(member);
    }

    /** JWT 만료 시간 계산 (기존 로직과 동일) */
    private long calculateExpirationSeconds() {
        return authProperties.accessMin() * 60;
    }
}
