package com.fliqo.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fliqo.domain.entity.MemberCredential;

public interface MemberCredentialRepository extends JpaRepository<MemberCredential, Long> {
    Optional<MemberCredential> findByMemberId(Long memberId);
}
