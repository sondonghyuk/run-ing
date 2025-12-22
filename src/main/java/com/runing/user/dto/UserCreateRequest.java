package com.runing.user.dto;

public record UserCreateRequest(
	String name,
	String email,
	String password,
	String nickname,
	String phoneNumber,
	String profileUrl
) {
}
