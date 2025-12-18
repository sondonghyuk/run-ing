package com.runing.common.error;

/**
 * 채팅 관련 에러들을 정의
 */
public enum ChatErrorCode implements ErrorCode {

	;

	private final String code;
	private final String message;

	ChatErrorCode(String code, String message) {
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
