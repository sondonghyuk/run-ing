package com.runing.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.runing.common.handler.OAuth2SuccessHandler;
import com.runing.jwt.filter.CustomUsernamePasswordAuthenticationFilter;
import com.runing.jwt.filter.JWTAuthenticationFilter;
import com.runing.jwt.service.CustomUserDetailsService;
import com.runing.jwt.service.JwtBlacklistService;
import com.runing.jwt.service.JwtRefreshTokenService;
import com.runing.jwt.util.JWTUtil;
import com.runing.oauth.router.OAuth2UserProviderRouter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JWTUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtRefreshTokenService jwtRefreshTokenService;
	private final JwtBlacklistService jwtBlacklistService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2UserProviderRouter oAuth2UserProviderRouter;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		// 허용 Origin
		config.setAllowedOrigins(List.of("http://localhost:3000"));

		// 허용 HTTP 메서드
		config.setAllowedMethods(List.of(
			"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
		));

		// 허용 헤더
		config.setAllowedHeaders(List.of(
			"Authorization",
			"Content-Type"
		));

		// 클라이언트에서 접근 가능한 헤더
		config.setExposedHeaders(List.of(
			"Authorization"
		));

		// 쿠키 포함 허용 (Refresh Token)
		config.setAllowCredentials(true);

		// preflight 캐시 시간
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager,
		CustomUserDetailsService customUserDetailsService) throws Exception {
		// cors
		http
			.cors(cors -> cors
				.configurationSource(corsConfigurationSource())
			);

		// csrf
		http.csrf(auth -> auth.disable());

		// form 로그인 방식
		http.formLogin(auth -> auth.disable());

		// http basic 인증 방식
		http.httpBasic(auth -> auth.disable());

		// 세션 설정
		http.sessionManagement(session -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		// 인가 규칙
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/users/signup").permitAll()
				.requestMatchers(HttpMethod.POST, "/login").permitAll() // 필터 처리 : 자체 로그인
				.requestMatchers("/email/**").permitAll()
				.requestMatchers("/auth/refresh").permitAll()
				.requestMatchers("/oauth2/**").permitAll() // 소셜 로그인 관련 요청
				.requestMatchers("/login/**").permitAll() // OAuth2 redirect 관련 요청
				.requestMatchers("/favicon.ico").permitAll()
				.anyRequest().authenticated()
			);

		// OAuth2
		http
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserProviderRouter)) // 사용자 정보 받아오기
				.successHandler(oAuth2SuccessHandler) // JWT 발급 및 응답
			);

		// 필터 추가
		http
			.addFilterBefore(new JWTAuthenticationFilter(jwtUtil, customUserDetailsService, jwtBlacklistService),
				CustomUsernamePasswordAuthenticationFilter.class)
			.addFilterAt(
				new CustomUsernamePasswordAuthenticationFilter(authenticationManager, jwtUtil, jwtRefreshTokenService),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
