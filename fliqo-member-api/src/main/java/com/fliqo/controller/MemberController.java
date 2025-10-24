package com.fliqo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fliqo.controller.dto.request.*;
import com.fliqo.controller.dto.response.*;
import com.fliqo.service.MemberService;
import com.fliqo.service.PhoneVerificationService;
import com.fliqo.service.dto.request.EmailCheckCommand;
import com.fliqo.service.dto.request.PhoneVerificationConfirmCommand;
import com.fliqo.service.dto.request.PhoneVerificationStartCommand;
import com.fliqo.service.dto.request.SignupCommand;
import com.fliqo.service.dto.response.EmailCheckResult;
import com.fliqo.service.dto.response.PhoneVerificationConfirmResult;
import com.fliqo.service.dto.response.PhoneVerificationStartResult;
import com.fliqo.service.dto.response.SignupResult;
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
}
