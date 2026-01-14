package com.runing.common.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runing.common.config.JwtCookieProperties;
import com.runing.common.response.ApiResponse;
import com.runing.common.response.TokenResponse;
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
	private final ObjectMapper objectMapper;
	private final JwtCookieProperties jwtCookieProperties;

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

		// RefreshToken은 HttpOnly 쿠키로만 내려주기
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(jwtCookieProperties.isHttpOnly())
			.secure(jwtCookieProperties.isSecure())
			.path("/")
			.maxAge(TimeUnit.DAYS.toSeconds(jwtCookieProperties.getMaxAgeDays()))
			.sameSite(jwtCookieProperties.getSameSite())
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		// AccessToken은 JSON으로 응답
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		objectMapper.writeValue(response.getWriter(), new ApiResponse<>("success", new TokenResponse(accessToken)));
	}
}
