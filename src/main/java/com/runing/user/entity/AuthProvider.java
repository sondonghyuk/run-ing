package com.runing.user.entity;

public enum AuthProvider {
	LOCAL("일반 가입"),
	KAKAO("카카오"),
	GOOGLE("구글"),
	NAVER("네이버");

	private final String description;

	AuthProvider(String description) {
		this.description = description;
	}
}
