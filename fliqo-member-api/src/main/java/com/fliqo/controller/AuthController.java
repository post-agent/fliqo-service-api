package com.fliqo.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.fliqo.config.AuthProperties;
import com.fliqo.controller.dto.response.TokenResponseDto;
import com.fliqo.domain.entity.AuthProvider;
import com.fliqo.exception.BadRequestException;
import com.fliqo.exception.ErrorCode;
import com.fliqo.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 소셜 로그인 엔드포인트 컨트롤러 카카오, 네이버 OAuth 인증 플로우를 처리 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthProperties authProperties;

    /**
     * 카카오 로그인 시작 사용자를 카카오 인증 페이지로 리다이렉트
     *
     * <p>GET /api/auth/kakao
     */
    @GetMapping("/kakao")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        String authUrl = buildAuthorizationUrl(AuthProvider.KAKAO);
        response.sendRedirect(authUrl);
    }

    /**
     * 네이버 로그인 시작 사용자를 네이버 인증 페이지로 리다이렉트
     *
     * <p>GET /api/auth/naver
     */
    @GetMapping("/naver")
    public void naverLogin(HttpServletResponse response) throws IOException {
        String authUrl = buildAuthorizationUrl(AuthProvider.NAVER);
        response.sendRedirect(authUrl);
    }

    /**
     * 카카오 로그인 콜백 카카오 인증 후 리다이렉트되는 엔드포인트
     *
     * <p>GET /api/auth/kakao/callback?code=xxxxx
     */
    @GetMapping("/kakao/callback")
    public RedirectView kakaoCallback(@RequestParam("code") String code) {
        return handleCallback(AuthProvider.KAKAO, code);
    }

    /**
     * 네이버 로그인 콜백 네이버 인증 후 리다이렉트되는 엔드포인트
     *
     * <p>GET /api/auth/naver/callback?code=xxxxx
     */
    @GetMapping("/naver/callback")
    public RedirectView naverCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {
        // 네이버는 state 파라미터도 전달됨 (CSRF 방지용)
        return handleCallback(AuthProvider.NAVER, code);
    }

    /** 인증 URL 생성 */
    private String buildAuthorizationUrl(AuthProvider provider) {

        AuthProperties.ProviderConfig config = getProviderConfig(provider);
        String redirectUri = getCallbackUri(provider);

        StringBuilder urlBuilder = new StringBuilder(config.authorizationUri());
        urlBuilder.append("?client_id=").append(config.clientId());
        urlBuilder
                .append("&redirect_uri=")
                .append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
        urlBuilder.append("&response_type=code");

        // 네이버는 state 필요 (CSRF 방지)
        if (provider == AuthProvider.NAVER) {
            String state = generateState();
            urlBuilder.append("&state=").append(state);
        }

        return urlBuilder.toString();
    }

    /** 콜백 처리 공통 로직 */
    private RedirectView handleCallback(AuthProvider provider, String code) {
        try {
            String redirectUri = getCallbackUri(provider);
            TokenResponseDto tokenResponse = authService.login(provider, code, redirectUri);

            // 프론트엔드로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
            String successUrl =
                    String.format(
                            "%s?token=%s&expiresIn=%d",
                            authProperties.redirect().successUrl(),
                            tokenResponse.accessToken(),
                            tokenResponse.expirationInSeconds());

            return new RedirectView(successUrl);

        } catch (Exception e) {
            log.error("auth callback failed: provider={}, error={}", provider, e.getMessage());

            String failureUrl =
                    String.format(
                            "%s?error=%s",
                            authProperties.redirect().failureUrl(),
                            URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));

            return new RedirectView(failureUrl);
        }
    }

    /** 콜백 URI 생성 */
    private String getCallbackUri(AuthProvider provider) {
        return String.format(
                "%s/api/auth/%s/callback", authProperties.baseUrl(), provider.name().toLowerCase());
    }

    /** CSRF 방지용 state 생성 (간단한 예시) */
    private String generateState() {
        return java.util.UUID.randomUUID().toString();
    }

    /** 제공자 설정 조회 */
    private AuthProperties.ProviderConfig getProviderConfig(AuthProvider provider) {
        AuthProperties.ProviderConfig config = authProperties.provider(provider.name());
        if (config == null) {
            throw new BadRequestException(ErrorCode.AUTH_PROVIDER_NOT_CONFIGURED);
        }
        return config;
    }
}
