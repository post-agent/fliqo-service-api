package com.fliqo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fliqo.domain.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}
