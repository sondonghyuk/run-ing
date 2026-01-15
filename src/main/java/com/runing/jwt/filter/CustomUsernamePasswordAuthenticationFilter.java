package com.runing.jwt.filter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.runing.common.response.SendErrorResponse;
import com.runing.jwt.dto.CustomUserDetails;
import com.runing.jwt.dto.JWTUserDto;
import com.runing.jwt.service.JwtRefreshTokenService;
import com.runing.jwt.util.JWTUtil;
import com.runing.user.entity.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final JwtRefreshTokenService jwtRefreshTokenService;

	private static final long REFRESH_EXPIRATION_DAYS = 7;

	// 로그인 요청 시 사용자 인증 처리
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		// 로그인 시도
		String username = obtainUsername(request);
		log.debug("로그인 시도 - username: {}", username);
		String password = obtainPassword(request);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

		return authenticationManager.authenticate(authToken);
	}

	// 로그인 성공 시 JWT 토큰 발급
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {
		try {
			// 사용자 정보 추출
			CustomUserDetails customUserDetails = (CustomUserDetails)authResult.getPrincipal();

			UUID userUuid = customUserDetails.getUserUuid();
			String email = customUserDetails.getEmail();
			String name = customUserDetails.getName();
			Role role = customUserDetails.getRole();

			// jwt 토큰 생성
			JWTUserDto user = new JWTUserDto(userUuid, email, name, role);

			String accessToken = jwtUtil.createAccessToken(user);
			String refreshToken = jwtUtil.createRefreshToken();

			jwtRefreshTokenService.save(userUuid, refreshToken, REFRESH_EXPIRATION_DAYS, TimeUnit.DAYS);

			// AccessToken 응답
			response.addHeader("Authorization", "Bearer " + accessToken);

			// RefreshToken 응답
			ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(60L * 60 * 24 * REFRESH_EXPIRATION_DAYS)
				.sameSite("Lax")
				.build();
			response.addHeader("Set-Cookie", cookie.toString());

			log.info("로그인 성공 - name: {}, role: {}", name, role);
		} catch (Exception e) {
			log.error("JWT 토큰 생성 중 오류 발생", e);
			SendErrorResponse.sendResponse(response,"토큰 생성 오류");
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		log.warn("로그인 실패 - IP: {}, 이유: {}", request.getRemoteAddr(), failed.getMessage());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
