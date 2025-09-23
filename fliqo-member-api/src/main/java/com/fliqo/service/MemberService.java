package com.fliqo.service;

import org.springframework.stereotype.Service;

import com.fliqo.domain.MemberRepository;
import com.fliqo.service.dto.EmailCheckCommand;
import com.fliqo.service.dto.EmailCheckResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public EmailCheckResult checkEmail(EmailCheckCommand cmd) {
        boolean exists = memberRepository.existsByEmail(cmd.email());
        return new EmailCheckResult(exists);
    }
}
