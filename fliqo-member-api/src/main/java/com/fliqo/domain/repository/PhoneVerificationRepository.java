package com.fliqo.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fliqo.domain.entity.PhoneVerification;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, String> {
    Optional<PhoneVerification> findByVerificationToken(String token);
}
