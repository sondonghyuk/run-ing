package com.runing.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<Void> requestEmailVerification(
		@Valid @RequestBody EmailVerificationRequest request
	){
		emailVerificationService.sendVerificationEmail(request.email());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/verify-link")
	public ResponseEntity<Void> verifyLink(
		@RequestParam String token
	){
		boolean result = emailVerificationService.verifyEmail(token);
		if(result){
			return ResponseEntity.ok().build();
		}else{
			return ResponseEntity.badRequest().build();
		}
	}
}
