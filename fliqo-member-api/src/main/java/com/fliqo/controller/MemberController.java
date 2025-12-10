package com.fliqo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fliqo.controller.dto.request.*;
import com.fliqo.controller.dto.response.*;
import com.fliqo.dto.CommonResponse;
import com.fliqo.service.MemberService;
import com.fliqo.service.PasswordResetService;
import com.fliqo.service.PhoneVerificationService;
import com.fliqo.service.dto.request.*;
import com.fliqo.service.dto.response.*;
import com.fliqo.service.validator.MemberPolicyValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final PhoneVerificationService phoneVerificationService;
    private final MemberPolicyValidator memberPolicyValidator;
    private final PasswordResetService passwordResetService;

    @PostMapping("/email-check")
    public ResponseEntity<CommonResponse<EmailCheckResponse>> check(
            @Valid @RequestBody EmailCheckRequest emailCheckRequest) {
        EmailCheckCommand emailCheckCmd = EmailCheckCommand.of(emailCheckRequest.email());
        EmailCheckResult emailCheckResult = memberService.checkEmail(emailCheckCmd);

        EmailCheckResponse emailCheckResponse = EmailCheckResponse.of(emailCheckResult.exists());
        return ResponseEntity.ok(CommonResponse.success(emailCheckResponse));
    }

    @PostMapping("/phone/verify/request")
    public ResponseEntity<CommonResponse<PhoneVerifyRequestResponse>> phoneVerifyRequest(
            @Valid @RequestBody PhoneVerifyStartRequest phoneVerifyStartRequest) {
        PhoneVerificationStartResult phoneVerificationStartResult =
                phoneVerificationService.start(
                        PhoneVerificationStartCommand.of(
                                phoneVerifyStartRequest.phoneNumber(),
                                phoneVerifyStartRequest.purpose()));
        return ResponseEntity.ok(
                CommonResponse.success(
                        PhoneVerifyRequestResponse.of(
                                phoneVerificationStartResult.verificationId(),
                                phoneVerificationStartResult.expiresInMinutes())));
    }

    @PostMapping("/phone/verify/confirm")
    public ResponseEntity<CommonResponse<PhoneVerifyConfirmResponse>> phoneVerifyConfirm(
            @Valid @RequestBody PhoneVerifyConfirmRequest phoneVerifyConfirmRequest) {
        PhoneVerificationConfirmResult phoneVerificationConfirmResult =
                phoneVerificationService.confirm(
                        PhoneVerificationConfirmCommand.of(
                                phoneVerifyConfirmRequest.verificationId(),
                                phoneVerifyConfirmRequest.code()));

        return ResponseEntity.ok(
                CommonResponse.success(
                        PhoneVerifyConfirmResponse.of(
                                phoneVerificationConfirmResult.verificationToken())));
    }

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {
        memberPolicyValidator.validateOrThrow(
                signupRequest.password(), signupRequest.passwordConfirm());

        SignupResult signupResult = memberService.signup(signupRequest.toCommand());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SignupResponse.builder()
                                .memberUuid(signupResult.memberUuid())
                                .email(signupResult.email())
                                .name(signupResult.name())
                                .phoneNumber(signupResult.phoneNumber())
                                .build()));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<TokenResponseDto>> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {
        TokenResponseDto tokenResponseDto =
                memberService.login(loginRequestDto.email(), loginRequestDto.password());
        return ResponseEntity.ok(CommonResponse.success(tokenResponseDto));
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<CommonResponse<PasswordResetStartResult>> requestPasswordReset(
            @Valid @RequestBody PasswordResetStartRequest passwordResetStartRequest) {
        PasswordResetStartResult passwordResetStartResult =
                passwordResetService.start(
                        PasswordResetStartCommand.of(
                                passwordResetStartRequest.email(),
                                passwordResetStartRequest.phoneNumber(),
                                passwordResetStartRequest.purpose()));
        return ResponseEntity.ok(CommonResponse.success(passwordResetStartResult));
    }

    @PostMapping("/password/reset/verify")
    public ResponseEntity<CommonResponse<PasswordResetVerifyResult>> verifyPhoneCode(
            @Valid @RequestBody PasswordResetVerifyRequest passwordResetVerifyRequest) {
        PasswordResetVerifyResult passwordResetVerifyResult =
                passwordResetService.verify(
                        PasswordResetVerifyCommand.of(
                                passwordResetVerifyRequest.verificationId(),
                                passwordResetVerifyRequest.code()));
        return ResponseEntity.ok(CommonResponse.success(passwordResetVerifyResult));
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<CommonResponse<String>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest passwordResetConfirmRequest) {
        passwordResetService.confirm(
                PasswordResetConfirmCommand.of(
                        passwordResetConfirmRequest.resetToken(),
                        passwordResetConfirmRequest.newPassword()));
        return ResponseEntity.ok(CommonResponse.success("새로운 비밀번호로 변경되었습니다."));
    }

    @PostMapping("email/find/request")
    public ResponseEntity<CommonResponse<PhoneVerifyRequestResponse>> emailFindRequest(
            @Valid @RequestBody PhoneVerifyStartRequest phoneVerifyStartRequest) {
        PhoneVerificationStartResult phoneVerificationStartResult =
                phoneVerificationService.start(
                        PhoneVerificationStartCommand.of(
                                phoneVerifyStartRequest.phoneNumber(),
                                phoneVerifyStartRequest.purpose()));

        return ResponseEntity.ok(
                CommonResponse.success(
                        PhoneVerifyRequestResponse.of(
                                phoneVerificationStartResult.verificationId(),
                                phoneVerificationStartResult.expiresInMinutes())));
    }

    @PostMapping("email/find/confirm")
    public ResponseEntity<CommonResponse<EmailFindConfirmResponse>> emailFindConfirm(
            @Valid @RequestBody EmailFindConfirmRequest emailFindVerifyRequest) {
        EmailFindConfirmResult emailFindConfirmResult =
                memberService.emailFindByPhone(
                        EmailFindConfirmCommand.of(
                                emailFindVerifyRequest.verificationId(),
                                emailFindVerifyRequest.code()));

        return ResponseEntity.ok(
                CommonResponse.success(
                        EmailFindConfirmResponse.of(emailFindConfirmResult.maskedEmail())));
    }
}
