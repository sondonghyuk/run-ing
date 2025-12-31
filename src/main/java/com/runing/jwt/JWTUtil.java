package com.runing.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 관련 메서드를 제공하는 클래스
 */
@Slf4j
@Component
public class JWTUtil {

	private SecretKey secretKey;
	private final Long accessTokenExpiration; // Access Token 만료 시간

	public JWTUtil(@Value("${spring.jwt.secret}") String secret,
		@Value("${spring.jwt.access-token-expiration}") Long accessTokenExpiration
	) {
		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
		this.accessTokenExpiration = accessTokenExpiration;
	}

	// 토큰에서 정보 추출
	public String getUsername(String token) {
		return getClaims(token)
			.get("username", String.class);
	}

	public String getRole(String token) {
		return getClaims(token)
			.get("role", String.class);
	}

	public Date getExpired(String token) {
		return getClaims(token).getExpiration();
	}

	public Date getIssuedAt(String token) {
		return getClaims(token).getIssuedAt();
	}

	// 토큰 만료 여부 확인
	public boolean isExpired(String token) {
		try {
			return getClaims(token)
				.getExpiration()
				.before(new Date());
		} catch (ExpiredJwtException e) {
			log.warn("토큰이 만료되었습니다: {}", e.getMessage());
			return true; // 오류 발생 = 만료로 간주 = 접근 차단
		} catch (Exception e) {
			log.error("토큰 검증 중 오류 발생 : {}", e.getMessage());
			return true; // 오류 발생 = 만료로 간주 = 접근 차단
		}
	}

	// 토큰 유효성 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey) // 서명 검증
				.build()
				.parseSignedClaims(token); // 파싱 실패하면 예외 발생
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("잘못된 JWT 서명입니다: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.warn("만료된 JWT 토큰입니다.: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 JWT 토큰입니다.: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT 토큰이 잘못되었습니다.: {}", e.getMessage());
		}
		return false;
	}

	// 토큰 생성
	public String createJwt(String username, String role) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + accessTokenExpiration);

		String token = Jwts.builder()
			.claim("username", username)
			.claim("role", role)
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey)
			.compact();

		log.info("JWT 토큰 생성 완료 - username: {}, role: {}, expiredMs: {}",
			username, role, expiration);

		return token;
	}

	// Claims 추출
	private Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
