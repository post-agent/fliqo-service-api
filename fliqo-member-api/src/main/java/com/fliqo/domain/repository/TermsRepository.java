package com.fliqo.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fliqo.domain.entity.Terms;

public interface TermsRepository extends JpaRepository<Terms, Long> {

    Optional<Terms> findTopByCodeOrderByVersionDesc(String code);
}
