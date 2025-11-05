package com.fliqo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fliqo.controller.dto.request.*;
import com.fliqo.controller.dto.response.*;
import com.fliqo.service.EmailFindService;
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
    private final EmailFindService emailFindService;

    @PostMapping("/email-check")
    public ResponseEntity<ApiResponse<EmailCheckResponse>> check(
            @Valid @RequestBody EmailCheckRequest emailCheckRequest) {
        EmailCheckCommand emailCheckCmd = EmailCheckCommand.of(emailCheckRequest.email());
        EmailCheckResult emailCheckResult = memberService.checkEmail(emailCheckCmd);

        EmailCheckResponse emailCheckResponse = EmailCheckResponse.of(emailCheckResult.exists());
        return ResponseEntity.ok(ApiResponse.ok(emailCheckResponse));
    }

    @PostMapping("/phone/verify/request")
    public ResponseEntity<ApiResponse<PhoneVerifyRequestResponse>> phoneVerifyRequest(
            @Valid @RequestBody PhoneVerifyStartRequest phoneVerifyStartRequest) {
        PhoneVerificationStartResult phoneVerificationStartResult =
                phoneVerificationService.start(
                        PhoneVerificationStartCommand.of(phoneVerifyStartRequest.phoneNumber()));
        return ResponseEntity.ok(
                ApiResponse.ok(
                        PhoneVerifyRequestResponse.of(
                                phoneVerificationStartResult.verificationId(),
                                phoneVerificationStartResult.expiresInMinutes())));
    }

    @PostMapping("/phone/verify/confirm")
    public ResponseEntity<ApiResponse<PhoneVerifyConfirmResponse>> phoneVerifyConfirm(
            @Valid @RequestBody PhoneVerifyConfirmRequest phoneVerifyConfirmRequest) {
        PhoneVerificationConfirmResult phoneVerificationConfirmResult =
                phoneVerificationService.confirm(
                        PhoneVerificationConfirmCommand.of(
                                phoneVerifyConfirmRequest.verificationId(),
                                phoneVerifyConfirmRequest.code()));

        return ResponseEntity.ok(
                ApiResponse.ok(
                        PhoneVerifyConfirmResponse.of(
                                phoneVerificationConfirmResult.verificationToken())));
    }

    @PostMapping("/email/find/request")
    public ResponseEntity<ApiResponse<EmailFindStartResponse>> requestEmailFind(
            @Valid @RequestBody EmailFindStartRequest emailFindStartRequest) {
        EmailFindStartResult emailFindStartResult =
                emailFindService.start(EmailFindStartCommand.of(emailFindStartRequest.phoneNumber()));

        return ResponseEntity.ok(
                ApiResponse.ok(
                        EmailFindStartResponse.of(
                                emailFindStartResult.verificationId(),
                                emailFindStartResult.expiresInMinutes())));
    }

    @PostMapping("/email/find/verify")
    public ResponseEntity<ApiResponse<EmailFindVerifyResponse>> verifyEmailFind(
            @Valid @RequestBody EmailFindVerifyRequest emailFindVerifyRequest) {
        EmailFindResult emailFindResult =
                emailFindService.verify(
                        EmailFindVerifyCommand.of(
                                emailFindVerifyRequest.verificationId(),
                                emailFindVerifyRequest.code()));

        String message =
                emailFindResult.found()
                        ? String.format("등록된 이메일은 %s 입니다.", emailFindResult.maskedEmail())
                        : "등록된 회원정보가 없습니다.";

        return ResponseEntity.ok(
                ApiResponse.ok(
                        EmailFindVerifyResponse.of(
                                emailFindResult.found(), emailFindResult.maskedEmail(), message)));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {
        memberPolicyValidator.validateOrThrow(
                signupRequest.password(), signupRequest.passwordConfirm());

        SignupResult signupResult =
                memberService.signup(
                        SignupCommand.builder()
                                .email(signupRequest.email())
                                .rawPassword(signupRequest.password())
                                .name(signupRequest.name())
                                .phoneNumber(signupRequest.phoneNumber())
                                .phoneVerificationToken(signupRequest.phoneVerificationToken())
                                .build());

        return ResponseEntity.ok(
                ApiResponse.ok(
                        SignupResponse.builder()
                                .memberUuid(signupResult.memberUuid())
                                .email(signupResult.email())
                                .name(signupResult.name())
                                .phoneNumber(signupResult.phoneNumber())
                                .build()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {
        TokenResponseDto tokenResponseDto =
                memberService.login(loginRequestDto.email(), loginRequestDto.password());
        return ResponseEntity.ok(tokenResponseDto);
    }

    @GetMapping("/me")
    public Map<String, Object> me(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesCsv) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-User-Id");
        }
        return Map.of(
                "userId", userId,
                "roles", rolesCsv);
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<ApiResponse<PasswordResetStartResult>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        PasswordResetStartResult passwordResetStartResult =
                passwordResetService.start(
                        PasswordResetStartCommand.of(
                                passwordResetRequest.email(), passwordResetRequest.phoneNumber()));
        return ResponseEntity.ok(ApiResponse.ok(passwordResetStartResult));
    }

    @PostMapping("/password/reset/verify")
    public ResponseEntity<ApiResponse<PasswordResetVerifyResult>> verifyPhoneCode(
            @Valid @RequestBody PasswordResetVerifyRequest passwordResetVerifyRequest) {
        PasswordResetVerifyResult passwordResetVerifyResult =
                passwordResetService.verify(
                        PasswordResetVerifyCommand.of(
                                passwordResetVerifyRequest.verificationId(),
                                passwordResetVerifyRequest.code()));
        return ResponseEntity.ok(ApiResponse.ok(passwordResetVerifyResult));
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<ApiResponse<String>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest passwordResetConfirmRequest) {
        passwordResetService.confirm(
                PasswordResetConfirmCommand.of(
                        passwordResetConfirmRequest.resetToken(),
                        passwordResetConfirmRequest.newPassword()));
        return ResponseEntity.ok(ApiResponse.ok("새로운 비밀번호로 변경되었습니다."));
    }
}
