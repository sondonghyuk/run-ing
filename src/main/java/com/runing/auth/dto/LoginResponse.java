package com.runing.auth.dto;

import com.runing.user.dto.UserDto;

public record LoginResponse(
	String accessToken,
	String refreshToken,
	UserDto user
) {
}
