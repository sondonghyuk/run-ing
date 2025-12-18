package com.runing.common.error;

/**
 * 팔로우 관련 에러들을 정의
 */
public enum FollowErrorCode implements ErrorCode {

	;

	private final String code;
	private final String message;

	FollowErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
