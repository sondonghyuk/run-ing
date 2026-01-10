package com.runing.jwt.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.runing.jwt.util.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtRefreshTokenService {

	private final RedisTemplate<String, String> redisTemplate;
	private final JWTUtil jwtUtil;
	private final String PREFIX = "refresh:user"; // Redis Key 충돌 방지용

	// Refresh Token 을 Redis 에 저장하면서 TTL 설정
	public void save(UUID userUuId, String token, long duration, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(PREFIX + userUuId, token, duration, timeUnit);
	}

	// 특정 사용자에게 저장된 Refresh Token 조회
	public String get(UUID userUuId) {
		return redisTemplate.opsForValue().get(PREFIX+userUuId);
	}

	// Refresh Token 삭제
	public void delete(UUID userUuId) {
		redisTemplate.delete(PREFIX+userUuId);
	}

	// 현재 유효한 토큰인지 검사
	public boolean isValid(UUID userUuId, String token) {
		String saved = get(userUuId);
		return saved != null && saved.equals(token) && jwtUtil.validateToken(saved);
	}
}
