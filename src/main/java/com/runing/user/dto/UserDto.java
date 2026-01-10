package com.runing.user.dto;

import java.util.UUID;

import com.runing.user.entity.Role;

public record UserDto(
	UUID userUuId, // 외부 식별자
	String email,
	String name,
	String nickname,
	String phoneNumber,
	String profileUrl,
	Role role
) {
}
