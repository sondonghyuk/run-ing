package com.runing.user.dto;

public record ProfileDto(
	String email,
	String name,
	String nickname,
	String phoneNumber,
	String profileUrl,
	String region
) {
}
