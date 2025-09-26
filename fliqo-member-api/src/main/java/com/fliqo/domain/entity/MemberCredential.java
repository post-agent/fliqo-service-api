package com.fliqo.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_member_credential")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCredential {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "password_algo", nullable = false, length = 50)
    private PasswordAlgo passwordAlgo = PasswordAlgo.BCRYPT; // 'bcrypt'

    @Builder.Default
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    public void resetFailures() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }

    public void lockAccount(int lockDurationMinutes) {
        this.lockedUntil = LocalDateTime.now().plusMinutes(lockDurationMinutes);
    }

    /**
     * 새 {@link MemberCredential} 인스턴스를 생성합니다.
     *
     * <p>주어진 회원과 해시된 비밀번호를 바인딩하여 자격 증명 객체를 만듭니다. 이 메서드는 비밀번호의 해싱을 수행하지 않으며, <b>이미 해시된 값</b>을 입력받는다는
     * 전제하에 동작합니다.
     *
     * @param member 자격 증명을 소유할 회원 (null 불가)
     * @param passwordHash 해시된 비밀번호 문자열 (raw 비밀번호 금지)
     * @return 생성된 {@link MemberCredential} 객체
     */
    public static MemberCredential createNew(Member member, String passwordHash) {
        return MemberCredential.builder().member(member).passwordHash(passwordHash).build();
    }

    @PrePersist
    void prePersist() {
        if (passwordChangedAt == null) this.passwordChangedAt = LocalDateTime.now();
    }
}
