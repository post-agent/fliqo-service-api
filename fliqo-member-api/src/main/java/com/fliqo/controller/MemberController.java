package com.fliqo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fliqo.controller.dto.request.EmailCheckRequest;
import com.fliqo.controller.dto.request.PhoneVerifyConfirmRequest;
import com.fliqo.controller.dto.request.PhoneVerifyStartRequest;
import com.fliqo.controller.dto.request.SignupRequest;
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
            @Valid @RequestBody EmailCheckRequest req) {
        EmailCheckCommand cmd = new EmailCheckCommand(req.email().toLowerCase());
        EmailCheckResult result = memberService.checkEmail(cmd);

        EmailCheckResponse resp = EmailCheckResponse.of(result.exists());
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @PostMapping("/phone/verify/request")
    public ResponseEntity<ApiResponse<PhoneVerifyRequestResponse>> phoneVerifyRequest(
            @Valid @RequestBody PhoneVerifyStartRequest req) {
        PhoneVerificationStartResult res =
                phoneVerificationService.start(PhoneVerificationStartCommand.of(req.phoneNumber()));
        return ResponseEntity.ok(
                ApiResponse.ok(
                        PhoneVerifyRequestResponse.of(
                                res.verificationId(), res.expiresInMinutes())));
    }

    @PostMapping("/phone/verify/confirm")
    public ResponseEntity<ApiResponse<PhoneVerifyConfirmResponse>> phoneVerifyConfirm(
            @Valid @RequestBody PhoneVerifyConfirmRequest req) {
        PhoneVerificationConfirmResult res =
                phoneVerificationService.confirm(
                        PhoneVerificationConfirmCommand.of(req.verificationId(), req.code()));

        return ResponseEntity.ok(
                ApiResponse.ok(PhoneVerifyConfirmResponse.of(res.verificationToken())));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest req) {
        memberPolicyValidator.validateOrThrow(req.password(), req.passwordConfirm());

        SignupResult result =
                memberService.signup(
                        SignupCommand.builder()
                                .email(req.email())
                                .rawPassword(req.password())
                                .name(req.name())
                                .phoneNumber(req.phoneNumber())
                                .phoneVerificationToken(req.phoneVerificationToken())
                                .build());

        return ResponseEntity.ok(
                ApiResponse.ok(
                        SignupResponse.builder()
                                .memberUuid(result.memberUuid())
                                .email(result.email())
                                .name(result.name())
                                .phoneNumber(result.phoneNumber())
                                .build()));
    }
}
