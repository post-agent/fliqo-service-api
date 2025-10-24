package com.fliqo.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "tb_password_reset_token",
        indexes = {@Index(name = "idx_prt_token", columnList = "token", unique = true)})
@Getter
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant usedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Builder
    private PasswordResetToken(String token, Long memberId, Instant createdAt, Instant expiresAt) {
        this.token = token;
        this.memberId = memberId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static PasswordResetToken issue(Long memberId, long ttlSeconds) {
        Instant now = Instant.now();
        return PasswordResetToken.builder()
                .token(UUID.randomUUID().toString().replace("-", ""))
                .memberId(memberId)
                .createdAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .build();
    }

    public boolean isUsable(Instant now) {
        return usedAt == null && now.isBefore(expiresAt);
    }

    public void markUsed() {
        this.usedAt = Instant.now();
    }
}
