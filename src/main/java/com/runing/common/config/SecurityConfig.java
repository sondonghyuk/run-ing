package com.runing.common.config;

import java.util.Collections;
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

import com.runing.jwt.filter.CustomUsernamePasswordAuthenticationFilter;
import com.runing.jwt.filter.JWTAuthenticationFilter;
import com.runing.jwt.service.CustomUserDetailsService;
import com.runing.jwt.service.JwtRefreshTokenService;
import com.runing.jwt.util.JWTUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JWTUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtRefreshTokenService jwtRefreshTokenService;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowCredentials(true);
		config.setAllowedHeaders(Collections.singletonList("*"));
		config.setMaxAge(3600L);
		config.setExposedHeaders(List.of("Authorization","Refresh-Token"));

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
		http.csrf(auth->auth.disable());

		// form 로그인 방식
		http
			.formLogin(auth->auth.disable());

		// http basic 인증 방식
		http
			.httpBasic(auth->auth.disable());

		// 경로별 인가 방식
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/users/signup").permitAll()
				.requestMatchers(HttpMethod.POST, "/login").permitAll()
				.requestMatchers("/email/**").permitAll()
				.requestMatchers("/auth/refresh").permitAll()
				.anyRequest().authenticated()
			);

		// 필터 추가
		http
			.addFilterBefore(new JWTAuthenticationFilter(jwtUtil,customUserDetailsService),CustomUsernamePasswordAuthenticationFilter.class)
			.addFilterAt(new CustomUsernamePasswordAuthenticationFilter(authenticationManager,jwtUtil,jwtRefreshTokenService),
				UsernamePasswordAuthenticationFilter.class);

		// 세션 설정
		http
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);

		return http.build();
	}

}
