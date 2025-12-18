package com.runing.common.error;

/**
 * 러닝 코스 관련 에러들을 정의
 */
public enum CourseErrorCode implements ErrorCode {

	;

	private final String code;
	private final String message;

	CourseErrorCode(String code, String message) {
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
