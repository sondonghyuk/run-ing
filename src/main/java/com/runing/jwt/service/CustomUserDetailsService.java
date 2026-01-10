package com.runing.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.runing.jwt.dto.CustomUserDetails;
import com.runing.user.entity.User;
import com.runing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("사용자 조회 시도 : {}",username);

		// 이메일로 사용자 조회
		User user = userRepository.findByEmail(username)
			.orElseThrow(() -> {
				log.warn("사용자를 찾을 수 없습니다 - email: {}", username);
				return new UsernameNotFoundException("사용자를 찾을 수 없습니다. - email : " +username);
			});
		log.debug("사용자 로드 성공 - email: {}", username);

		return new CustomUserDetails(user);
	}
}
