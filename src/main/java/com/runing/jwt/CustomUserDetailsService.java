package com.runing.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.runing.common.error.BaseException;
import com.runing.common.error.UserErrorCode;
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
	public UserDetails loadUserByUsername(String username) {

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("사용자를 찾을 수 없습니다 - username: {}", username);
				return new BaseException(UserErrorCode.USER_NOT_FOUND); // 커스텀 예외
			});
		log.debug("사용자 로드 성공 - username: {}", username);

		return new CustomUserDetails(user);
	}
}
