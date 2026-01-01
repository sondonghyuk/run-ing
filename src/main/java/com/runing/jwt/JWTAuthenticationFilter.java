package com.runing.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 를 검증하기 위한 필터
 * - HTTP 요청마다 JWT 토큰을 검증하고 인증 정보를 SecurityContext 에 설정하는 필터
 */
@RequiredArgsConstructor
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// Authorization 헤더에서 JWT 토큰 추출
		String authorization = request.getHeader("Authorization");

		//Authorization 헤더 검증 : JWT 헤더가 없을 경우 다음 필터로 넘김
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			log.debug("JWT Token 을 request headres 에서 찾을 수 없음");
			filterChain.doFilter(request, response);
			return;
		}

		// Bearer 접두사 제거해 토큰 값 추출
		String token = authorization.substring(7);


		try{
			// JWT 유효성 검증
			if (!jwtUtil.validateToken(token)) {
				log.warn("JWT token 유효성 검증 실패");
				sendErrorResponse(response, "JWT token 유효성 실패");
				return;
			}

			// JWT 유효성 검증 성공 후
			Claims claims = jwtUtil.getClaims(token);
			String email = jwtUtil.getEmail(claims);

			log.debug("JWT token 인증 유저 : {}",email);

			// 데이터베이스에서 사용자 정보 조회
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

			// 사용자가 존재 하지 않는 경우
			if (userDetails == null) {
				log.warn("사용자가 존재하지 않음 : {}", email);
				sendErrorResponse(response, "사용자가 존재하지 않음");
			}

			// security 인증 토큰 생성
			Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());

			// 세션에 사용자 등록
			SecurityContextHolder.getContext().setAuthentication(authToken);

		}catch (Exception e) {
			log.error("JWT Authentication 실패 : {}",e.getMessage());
			sendErrorResponse(response, "Authentication 실패");
		}
		filterChain.doFilter(request, response);
	}

	private static void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"error\": \"" + message + "\"}");
	}
}
