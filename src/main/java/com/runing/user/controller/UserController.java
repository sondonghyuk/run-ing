package com.runing.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.runing.user.dto.UserCreateRequest;
import com.runing.user.dto.UserDto;
import com.runing.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateRequest request) {
		UserDto userDto = userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(userDto);
	}
}
