package com.fliqo.service.auth;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fliqo.config.AuthProperties;
import com.fliqo.domain.entity.AuthProvider;
import com.fliqo.exception.BadRequestException;
import com.fliqo.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** OAuth 제공자와 HTTP 통신을 담당하는 클라이언트 토큰 교환, 사용자 정보 조회 등의 공통 로직 처리 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClient {

    private final RestTemplate restTemplate;
    private final AuthProperties authProperties;

    /**
     * Authorization Code를 Access Token으로 교환
     *
     * @param provider 소셜 제공자 (KAKAO, NAVER)
     * @param code 인증 코드
     * @param redirectUri 콜백 URI
     * @return Access Token
     */
    public String getAccessToken(AuthProvider provider, String code, String redirectUri) {
        AuthProperties.ProviderConfig config = getProviderConfig(provider);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", config.clientId());
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // 네이버는 client_secret 필수, 카카오는 선택
        if (config.clientSecret() != null) {
            params.add("client_secret", config.clientSecret());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(config.tokenUri(), request, Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("access_token")) {
                throw new BadRequestException(ErrorCode.AUTH_TOKEN_REQUEST_FAILED);
            }

            return (String) body.get("access_token");
        } catch (Exception e) {
            log.error("Failed to get access token from {}: {}", provider, e.getMessage());
            throw new BadRequestException(ErrorCode.AUTH_TOKEN_REQUEST_FAILED);
        }
    }

    /**
     * Access Token으로 사용자 정보 조회
     *
     * @param provider 소셜 제공자
     * @param accessToken Access Token
     * @return 사용자 정보 Map
     */
    public Map<String, Object> getUserInfo(AuthProvider provider, String accessToken) {
        AuthProperties.ProviderConfig config = getProviderConfig(provider);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            config.userInfoUri(),
                            HttpMethod.GET,
                            request,
                            new ParameterizedTypeReference<>() {});

            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get user info from {}: {}", provider, e.getMessage());
            throw new BadRequestException(ErrorCode.AUTH_USER_INFO_REQUEST_FAILED);
        }
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
