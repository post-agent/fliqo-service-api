package com.fliqo.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fliqo.domain.entity.AuthProvider;
import com.fliqo.domain.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phoneNumber);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
