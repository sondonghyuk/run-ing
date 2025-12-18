package com.runing.common.error;

import org.springframework.http.HttpStatus;

/**
 * 팔로우 관련 에러들을 정의
 */
public enum FollowErrorCode implements ErrorCode {

	;

	private final HttpStatus status;
	private final String code;
	private final String message;

	FollowErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
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
