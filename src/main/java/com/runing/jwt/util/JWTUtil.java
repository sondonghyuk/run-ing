package com.runing.jwt.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.runing.jwt.dto.JWTUserDto;
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

	// Refresh 토큰 생성
	public String createRefreshToken(){
		return Jwts.builder()
			.id(UUID.randomUUID().toString())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis()  + 1000L * 60 * 60 * 24 * 7)) // 7일
			.signWith(secretKey)
			.compact();
	}

	// AccessToken 토큰 생성
	public String createAccessToken(JWTUserDto user){
		return createJWT(user,accessTokenExpiration);
	}

	// JWT 생성
	private String createJWT(JWTUserDto user, Long expiration) {
		String jti = UUID.randomUUID().toString();
		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + expiration);

		String token = Jwts.builder()
			.id(jti)
			.subject(String.valueOf(user.userUuid()))
			.claim("email", user.email())
			.claim("name", user.name())
			.claim("role", user.role().name())
			.issuedAt(now)
			.expiration(expirationDate)
			.signWith(secretKey)
			.compact();

		log.info("JWT 토큰 생성 완료 - email: {} , name: {}, role: {}, expirationDate: {}",
			user.email(), user.name(), user.role(), expirationDate);

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
	public String getJti(String token) {
		return getClaims(token).getId();
	}

	public long getRemainingExpiration(String token) {
		Date expiration = getClaims(token).getExpiration();
		return expiration.getTime() - System.currentTimeMillis();
	}

	public UUID getUserUuid(Claims claims) {
		return UUID.fromString(claims.getSubject());
	}

	public String getEmail(Claims claims) {
		return claims.get("email", String.class);
	}

	public String getName(Claims claims) {
		return claims.get("name", String.class);
	}

	public Role getRole(Claims claims) {
		return Role.valueOf(claims.get("role", String.class));
	}

	public Date getExpired(Claims claims) {
		return claims.getExpiration();
	}

	public Date getIssuedAt(Claims claims) {
		return claims.getIssuedAt();
	}
}
