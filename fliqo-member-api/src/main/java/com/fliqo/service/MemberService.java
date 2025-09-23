package com.fliqo.service;

import com.fliqo.domain.MemberRepository;
import com.fliqo.dto.ApiResponse;
import com.fliqo.dto.EmailCheckData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public ApiResponse<EmailCheckData> checkEmail(String rawEmail) {
        String email = normalize(rawEmail);

        boolean exists = memberRepository.existsByEmail(email);

        String action = exists ? "login" : "signup";
        String code = exists ? "EMAIL_EXISTS" : "EMAIL_AVAILABLE";
        String message = exists ? "이미 가입된 이메일입니다." : "사용 가능한 이메일입니다.";

        return ApiResponse.ok(code, message, new EmailCheckData(exists, action, email));
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
