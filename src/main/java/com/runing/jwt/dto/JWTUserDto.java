package com.runing.jwt.dto;

import java.util.UUID;

import com.runing.user.entity.Role;

public record JWTUserDto(
	UUID userUuid,
	String email,
	String name,
	Role role
) {

}
