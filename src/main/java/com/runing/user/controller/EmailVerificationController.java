package com.runing.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.runing.common.response.ApiResponse;
import com.runing.user.dto.EmailVerificationRequest;
import com.runing.user.service.EmailVerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailVerificationController {
	private final EmailVerificationService emailVerificationService;

	@PostMapping("/verify-request")
	public ResponseEntity<ApiResponse<Void>> requestEmailVerification(
		@Valid @RequestBody EmailVerificationRequest request
	){
		emailVerificationService.sendVerificationEmail(request.email());
		return ResponseEntity.ok(new ApiResponse<>("인증 메일이 발송되었습니다."));
	}

	@GetMapping("/verify-link")
	public ResponseEntity<ApiResponse<Void>> verifyLink(
		@RequestParam String token
	){
		boolean result = emailVerificationService.verifyEmail(token);
		if(result){
			return ResponseEntity.ok(new ApiResponse<>("이메일 인증이 완료되었습니다."));
		}else{
			return ResponseEntity.status(HttpStatus.GONE).body(new ApiResponse<>("유효하지 않거나 만료된 링크입니다. 인증 메일을 다시 요청해주세요."));
		}
	}
}
