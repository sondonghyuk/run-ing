package com.runing.jwt.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.runing.jwt.util.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtBlacklistService {

	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;
	private final String PREFIX = "blacklist:access:";

	// 블랙리스트에 등록
	public void blacklist(String accessToken,long expirationMillis){
		String jti = jwtUtil.getJti(accessToken);

		redisTemplate.opsForValue().set(
			PREFIX+jti,"logout",expirationMillis, TimeUnit.MILLISECONDS
		);
	}

	// 블랙리스트에 있는지 확인
	public boolean isBlacklisted(String accessToken){
		try{
			String jti = jwtUtil.getJti(accessToken);
			return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
		}catch(Exception e){
			log.warn("만료/위조 토큰이여서 블랙리스트 체크 불가 : {}", e.getMessage());
			return false;
		}
	}
}
