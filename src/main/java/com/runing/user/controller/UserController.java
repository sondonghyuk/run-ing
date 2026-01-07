package com.runing.user.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.runing.jwt.CustomUserDetails;
import com.runing.user.dto.ProfileDto;
import com.runing.user.dto.UserCreateRequest;
import com.runing.user.dto.UserDto;
import com.runing.user.dto.UserUpdateRequest;
import com.runing.user.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateRequest request) {
		UserDto userDto = userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(userDto);
	}

	// 이메일 중복 확인
	@GetMapping("/check-email")
	public ResponseEntity<Void> checkEmail(
		@RequestParam
		@NotBlank(message = "이메일은 필수입니다.")
		@Email(message = "유효한 이메일 형식이어야 합니다.")
		@Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
		String email) {
		userService.checkDuplicateEmail(email);
		return ResponseEntity.ok().build();
	}

	// 닉네임 중복 확인
	@GetMapping("/check-nickname")
	public ResponseEntity<Void> checkNickname(
		@RequestParam
		@NotBlank(message = "닉네임은 필수입니다.")
		@Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
		String nickname) {
		userService.checkDuplicateNickname(nickname);
		return ResponseEntity.ok().build();
	}

	// 마이페이지
	@GetMapping("/me")
	public ResponseEntity<ProfileDto> myPage(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		UUID userId = customUserDetails.getUserUuid();
		ProfileDto profile = userService.getMyProfile(userId);
		return ResponseEntity.ok().body(profile);
	}

	// 회원 정보 수정
	@PatchMapping("/me")
	public ResponseEntity<ProfileDto> updateMyProfile(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestBody UserUpdateRequest userUpdateRequest
	){
		ProfileDto updateProfile = userService.updateProfile(customUserDetails.getUserUuid(), userUpdateRequest);
		return ResponseEntity.ok().body(updateProfile);
	}

}
