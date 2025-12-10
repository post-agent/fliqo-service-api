package com.fliqo.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tb_member_terms_agreement",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_member_terms_version",
                    columnNames = {"member_id", "terms_id", "agreed_version"})
        })
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTermsAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms;

    @Column(name = "agreed_version", nullable = false)
    private Integer agreedVersion;

    @Column(name = "agreed", nullable = false)
    private Boolean agreed;

    @Column(name = "agreed_at", nullable = false)
    private LocalDateTime agreedAt;

    @Column(name = "ip", length = 100)
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;
}
