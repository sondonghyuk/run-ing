package com.runing.user.service;

import org.springframework.stereotype.Service;

import com.runing.common.error.BaseException;
import com.runing.common.error.UserErrorCode;
import com.runing.user.dto.UserCreateRequest;
import com.runing.user.dto.UserDto;
import com.runing.user.entity.User;
import com.runing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	// 유저 생성
	public UserDto createUser(UserCreateRequest request){
		String email = request.email();
		String nickname = request.nickname();

		// 1. 중복검증
		if(userRepository.existsByEmail(email)){
			throw new BaseException(UserErrorCode.USER_EMAIL_EXISTS);
		}
		if(userRepository.existsByNickname(nickname)){
			throw new BaseException(UserErrorCode.USER_NICKNAME_EXISTS);
		}

		// 2. 유저 생성
		User user = new User(request.email(),request.password(),request.name(),request.nickname(),request.phoneNumber(),request.profileUrl());

		// 3. 유저 저장
		userRepository.save(user);

		// 4. userDto 변환
		UserDto userDto = userMapper.toDto(user);

		return userDto;
	}

}
