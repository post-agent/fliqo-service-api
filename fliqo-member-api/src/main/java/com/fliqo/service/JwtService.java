package com.fliqo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fliqo.config.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    void init() {
        String base64 = jwtProperties.getSecretBase64();
        if (base64 == null || base64.isBlank()) {
            throw new IllegalStateException("jwt.secret-base64 is null/blank"); // 원인 명확화
        }
        byte[] bytes = Decoders.BASE64.decode(base64);
        this.secretKey = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(String subjectEmail, List<String> roles) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(jwtProperties.getAccessMin(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(subjectEmail)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .id(UUID.randomUUID().toString())
                .claim("roles", roles)
                .signWith(secretKey)
                .compact();
    }
}
