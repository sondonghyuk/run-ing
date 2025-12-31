package com.runing.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.runing.user.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Slf4j
@Component
public class JWTUtil {

	private final SecretKey secretKey;
	private final Long accessTokenExpiration; // Access Token 만료 시간

	public JWTUtil(@Value("${spring.jwt.secret}") String secret,
		@Value("${spring.jwt.access-token-expiration}") Long accessTokenExpiration
	) {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8); // 문자열 바이트 변환
		this.secretKey = Keys.hmacShaKeyFor(keyBytes); // 키 길이 검증(HS256), 알고리즘(HmacSHA256) 자동 매핑
		this.accessTokenExpiration = accessTokenExpiration;
	}

	// 토큰 생성
	public String createJwt(Long userId, String email, String nickname, Role role) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + accessTokenExpiration);

		String token = Jwts.builder()
			.subject(String.valueOf(userId))
			.claim("email", email)
			.claim("nickname", nickname)
			.claim("role", role.name())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey)
			.compact();

		log.info("JWT 토큰 생성 완료 - email: {} , nickname: {}, role: {}, expiredAt: {}",
			email, nickname, role, expiration);

		return token;
	}

	// 토큰 유효성 검증
	public boolean validateToken(String token) {
		try {
			getClaims(token);
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

	// Claims 추출
	public Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	// 토큰에서 정보 추출
	public Long getUserId(String token) {
		return Long.valueOf(getClaims(token).getSubject());
	}

	public String getEmail(String token) {
		return getClaims(token).get("email", String.class);
	}

	public String getNickname(String token) {
		return getClaims(token)
			.get("nickname", String.class);
	}

	public Role getRole(String token) {
		return Role.valueOf(getClaims(token).get("role", String.class));
	}

	public Date getExpired(String token) {
		return getClaims(token).getExpiration();
	}

	public Date getIssuedAt(String token) {
		return getClaims(token).getIssuedAt();
	}
}
