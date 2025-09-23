package com.fliqo.controller;

import com.fliqo.dto.ApiResponse;
import com.fliqo.dto.EmailCheckData;
import com.fliqo.dto.EmailCheckRequest;
import com.fliqo.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/email-check")
    public ResponseEntity<ApiResponse<EmailCheckData>> emailCheck(
            @Valid @RequestBody EmailCheckRequest request) {

        ApiResponse<EmailCheckData> resp = memberService.checkEmail(request.email());
        return ResponseEntity.ok(resp);
    }
}