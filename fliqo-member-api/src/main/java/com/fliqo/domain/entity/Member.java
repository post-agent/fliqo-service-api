package com.fliqo.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "tb_member",
        indexes = {
            @Index(name = "ux_member_uuid", columnList = "member_uuid", unique = true),
            @Index(name = "ux_member_email", columnList = "email", unique = true),
            @Index(name = "ix_member_phone", columnList = "phone")
        })
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "member_uuid", nullable = false, length = 36, unique = true, updatable = false)
    private String memberUuid;

    @Email
    @Size(max = 320)
    @Column(name = "email", nullable = false, length = 320, unique = true)
    private String email;

    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @Column(name = "nickname", length = 50)
    private String nickname;

    @Size(max = 20)
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatus status;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Size(max = 50)
    @Column(name = "timezone", length = 50)
    private String timezone;

    @Size(max = 10)
    @Column(name = "locale", length = 10)
    private String locale;

    @Size(max = 255)
    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "owner_verified", nullable = false)
    private boolean ownerVerified;

    @Column(name = "onboarding_step", nullable = false)
    private short onboardingStep;

    @PrePersist
    private void prePersist() {
        if (this.memberUuid == null) {
            this.memberUuid = UUID.randomUUID().toString(); // DB의 gen_random_uuid() 대체
        }
        if (this.role == null) this.role = Role.USER;
        if (this.status == null) this.status = MemberStatus.ACTIVE;
        // 기본값들(스키마의 FALSE/ko-KR 등) 초기화
        // locale 기본값 필요 시:
        if (this.locale == null) this.locale = "ko-KR";
    }

    public void markEmailVerified() {
        this.emailVerified = true;
    }

    public void markPhoneVerified() {
        this.phoneVerified = true;
    }

    public void markLastLoginNow() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeProfileImage(String url) {
        this.profileImageUrl = url;
    }

    public void nextOnboardingStep() {
        this.onboardingStep++;
    }

    public boolean isOwner() {
        return this.role == Role.OWNER;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public void suspend() {
        this.status = MemberStatus.SUSPENDED;
    }

    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
    }

    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }
}
