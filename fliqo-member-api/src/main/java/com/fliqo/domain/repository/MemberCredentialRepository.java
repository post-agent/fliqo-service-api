package com.fliqo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fliqo.domain.entity.MemberCredential;

public interface MemberCredentialRepository extends JpaRepository<MemberCredential, Long> {}
