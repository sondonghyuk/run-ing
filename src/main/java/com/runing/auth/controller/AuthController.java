package com.runing.auth.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.runing.auth.dto.LoginResponse;
import com.runing.auth.dto.RefreshTokenRequest;
import com.runing.common.config.JwtCookieProperties;
import com.runing.common.response.ApiResponse;
import com.runing.jwt.dto.CustomUserDetails;
import com.runing.jwt.dto.JWTUserDto;
import com.runing.jwt.service.JwtBlacklistService;
import com.runing.jwt.service.JwtRefreshTokenService;
import com.runing.jwt.util.JWTUtil;
import com.runing.user.dto.UserDto;
import com.runing.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
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
	private final JwtBlacklistService jwtBlacklistService;
	private static final long REFRESH_EXPIRATION_DAYS = 7;
	private final JwtCookieProperties jwtCookieProperties;

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		// Refresh 검증
		if (!jwtRefreshTokenService.isValid(request.userUuid(), request.refreshToken())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Refresh Token 인증 실패"));
		}
		// 사용자 조회
		UserDto user = userService.findByUuid(request.userUuid());

		// 재발급
		JWTUserDto jwtUserDto = new JWTUserDto(user.userUuid(), user.email(), user.name(), user.role());
		String newAccessToken = jwtUtil.createAccessToken(jwtUserDto);
		String newRefreshToken = jwtUtil.createRefreshToken();

		// Redis 저장소 교체
		jwtRefreshTokenService.save(user.userUuid(), newRefreshToken, REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

		// HttpOnly 쿠키로 새 Refresh Token 설정
		ResponseCookie responseCookie = jwtCookieProperties.createCookie("refreshToken", newRefreshToken);

		// Access Token 만 응답 body
		LoginResponse response = new LoginResponse(newAccessToken, null, user);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, responseCookie.toString())
			.body(new ApiResponse<>("Refresh Token 인증 성공", response));
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		HttpServletRequest request
	) {
		// customUserDetails null 체크
		if(customUserDetails == null){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증되지 않은 사용자입니다."));
		}

		// 리프레시 토큰 삭제
		jwtRefreshTokenService.delete(customUserDetails.getUserUuid());

		// 액세스 토큰 추출
		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith("Bearer ")) {
			String accessToken = header.substring(7);
			try {
				long remainingTime = jwtUtil.getRemainingExpiration(accessToken);
				if (remainingTime > 0) {
					jwtBlacklistService.blacklist(accessToken, remainingTime);
				}
			} catch (Exception e) {
				log.debug("logout accessToken 처리 중 예외: {}", e.getMessage());
			}
		}

		return ResponseEntity.ok(new ApiResponse<>("로그아웃 성공"));
	}
}
