package com.runing.auth.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.runing.common.response.ApiResponse;
import com.runing.jwt.dto.CustomUserDetails;
import com.runing.jwt.dto.JWTUserDto;
import com.runing.jwt.util.JWTUtil;
import com.runing.jwt.service.JwtRefreshTokenService;
import com.runing.auth.dto.LoginResponse;
import com.runing.auth.dto.RefreshTokenRequest;
import com.runing.user.dto.UserDto;
import com.runing.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthController {

	private final JwtRefreshTokenService jwtRefreshTokenService;
	private final UserService userService;
	private final JWTUtil jwtUtil;
	private static final long REFRESH_EXPIRATION_DAYS = 7;

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request){
		// Refresh 검증
		if(!jwtRefreshTokenService.isValid(request.userUuid(),request.refreshToken())){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Refresh Token 인증 실패"));
		}
		// 사용자 조회
		UserDto user = userService.findById(request.userUuid());

		// 재발급
		JWTUserDto jwtUserDto = new JWTUserDto(user.userUuId(),user.email(),user.name(),user.role());
		String newAccessToken = jwtUtil.createAccessToken(jwtUserDto);
		String newRefreshToken = jwtUtil.createRefreshToken();

		// Redis 저장소 교체
		jwtRefreshTokenService.save(user.userUuId(),newRefreshToken,REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

		LoginResponse response = new LoginResponse(newAccessToken,newRefreshToken,user);
		return ResponseEntity.ok(new ApiResponse<>("Refresh Token 인증 성공",response));
	}


	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	){
		jwtRefreshTokenService.delete(customUserDetails.getUserUuid());
		return ResponseEntity.ok(new ApiResponse<>("로그아웃 성공"));
	}
}
