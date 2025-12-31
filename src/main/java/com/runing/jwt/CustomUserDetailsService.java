package com.runing.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("사용자를 찾을 수 없습니다 - username: {}", username);
				return new UsernameNotFoundException("사용자를 찾을 수 없습니다. - username : " +username);
			});
		log.debug("사용자 로드 성공 - username: {}", username);

		return new CustomUserDetails(user);
	}
}
