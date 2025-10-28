package com.fliqo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.fliqo.config.AuthProperties;
import com.fliqo.jwt.JwtClaimKeys;
import com.fliqo.jwt.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;
    private final AuthProperties authProperties;
    private SecretKey secretKey;

    @PostConstruct
    void init() {
        String base64 = jwtProperties.secret();
        if (base64 == null || base64.isBlank()) {
            throw new IllegalStateException("jwt.secret is null/blank"); // 원인 명확화
        }
        byte[] bytes = Decoders.BASE64.decode(base64);
        this.secretKey = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(String subjectEmail, List<String> roles) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(authProperties.accessMin(), ChronoUnit.MINUTES);

        String rolesClaim =
                jwtProperties.headerInjection() != null
                                && jwtProperties.headerInjection().rolesClaim() != null
                                && !jwtProperties.headerInjection().rolesClaim().isBlank()
                        ? jwtProperties.headerInjection().rolesClaim()
                        : JwtClaimKeys.ROLES;

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .audience()
                .add(jwtProperties.audience())
                .and()
                .subject(subjectEmail)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .id(UUID.randomUUID().toString())
                .claim(rolesClaim, roles)
                .signWith(secretKey)
                .compact();
    }
}
