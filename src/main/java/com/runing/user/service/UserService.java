package com.runing.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.runing.common.error.BaseException;
import com.runing.common.error.UserErrorCode;
import com.runing.user.dto.UserCreateRequest;
import com.runing.user.dto.UserDto;
import com.runing.user.entity.User;
import com.runing.user.mapper.UserMapper;
import com.runing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;

	// 유저 생성
	@Transactional
	public UserDto createUser(UserCreateRequest request) {

		// 1. 변수 설정
		String email = request.email();
		String username = request.name();
		String password = request.password();
		String phoneNumber = request.phoneNumber();

		log.debug("사용자 생성 시작: email={}, username={}", email, username);

		// 2. 중복검증
		if (userRepository.existsByEmail(email)) {
			throw new BaseException(UserErrorCode.USER_EMAIL_EXISTS);
		}

		// 3. 유저 생성
		// todo : profileURL -> BinaryContent
		String hashedPassword = passwordEncoder.encode(password);
		User user = new User(email, hashedPassword, username, phoneNumber, request.profileUrl());

		// 4. 유저 저장
		userRepository.save(user);
		log.info("사용자 생성 완료 : id={} , email={}, username={}", user.getId(), email, username);

		// 4. userDto 변환 후 반환
		return userMapper.toDto(user);
	}

}
