package com.fliqo.service.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fliqo.domain.repository.MemberRepository;
import com.fliqo.exception.MemberSignupError;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberPolicyValidator {
    private final MemberRepository memberRepository;

    /**
     * 주어진 이메일이 회원가입에 사용 가능한지(중복되지 않는지) 검증합니다.
     *
     * <p>이미 동일한 이메일을 가진 회원이 존재하면 {@link com.fliqo.exception.MemberSignupError#EMAIL_ALREADY_EXISTS}
     * 예외를 발생시켜 가입 절차를 중단합니다. 존재하지 않으면 아무 동작 없이 반환됩니다.
     *
     * @param email 중복 여부를 확인할 이메일(사전 검증/정규화된 값)
     * @throws RuntimeException {@link com.fliqo.exception.MemberSignupError#EMAIL_ALREADY_EXISTS} 에
     *     해당하는 예외가 발생합니다(이미 사용 중인 경우).
     * @see com.fliqo.exception.MemberSignupError#EMAIL_ALREADY_EXISTS
     */
    public void ensureEmailAvailable(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw MemberSignupError.EMAIL_ALREADY_EXISTS.get();
        }
    }

    // 8자 이상, 영문+숫자+특수문자 각각 최소 1개
    private static final Pattern PWD =
            Pattern.compile(
                    "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]).{8,}$");

    /**
     * 비밀번호와 확인값의 일치 및 정책(8자 이상, 영문·숫자·특수문자 각 1개 이상 포함)을 검증합니다.
     *
     * @param password 입력 비밀번호
     * @param passwordConfirm 비밀번호 확인값
     * @throws IllegalArgumentException 두 값이 일치하지 않거나 정책을 만족하지 않으면 발생
     */
    public void validateOrThrow(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw MemberSignupError.PASSWORD_MISMATCH.get();
        }

        if (!PWD.matcher(password).matches()) {
            throw MemberSignupError.PASSWORD_POLICY_VIOLATION.get();
        }
    }
}
