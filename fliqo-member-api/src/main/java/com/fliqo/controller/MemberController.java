package com.fliqo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fliqo.controller.dto.request.EmailCheckRequest;
import com.fliqo.controller.dto.response.ApiResponse;
import com.fliqo.controller.dto.response.EmailCheckResponse;
import com.fliqo.service.MemberService;
import com.fliqo.service.dto.EmailCheckCommand;
import com.fliqo.service.dto.EmailCheckResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/email-check")
    public ResponseEntity<ApiResponse<EmailCheckResponse>> check(
            @Valid @RequestBody EmailCheckRequest req) {
        EmailCheckCommand cmd = new EmailCheckCommand(req.email().toLowerCase());
        EmailCheckResult result = memberService.checkEmail(cmd);

        EmailCheckResponse resp = EmailCheckResponse.of(result.exists());
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }
}
