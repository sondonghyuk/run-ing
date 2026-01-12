package com.runing.common.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runing.common.response.ApiResponse;
import com.runing.jwt.dto.CustomUserDetails;
import com.runing.jwt.dto.JWTUserDto;
import com.runing.jwt.service.JwtRefreshTokenService;
import com.runing.jwt.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private static final long REFRESH_EXPIRATION_DAYS = 7;
	private final JWTUtil jwtUtil;
	private final JwtRefreshTokenService jwtRefreshTokenService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		// jwt 발급
		JWTUserDto jwtUserDto = new JWTUserDto(userDetails.getUserUuid(), userDetails.getEmail(), userDetails.getName(), userDetails.getRole());
		String accessToken = jwtUtil.createAccessToken(jwtUserDto);
		String refreshToken = jwtUtil.createRefreshToken();

		// Redis 저장
		jwtRefreshTokenService.save(userDetails.getUserUuid(),refreshToken,REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

		// RefreshToken → HttpOnly Cookie
		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(TimeUnit.DAYS.toSeconds(REFRESH_EXPIRATION_DAYS))
			.sameSite("Lax")
			.build();

		response.addHeader("Set-Cookie", cookie.toString());

		// AccessToken을 Map으로 전달
		Map<String, String> tokenData = Map.of("accessToken", accessToken);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(
			new ApiResponse<>("OAuth2 로그인 성공", tokenData)
		));
	}
}
