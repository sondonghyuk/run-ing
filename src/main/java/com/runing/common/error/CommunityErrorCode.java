package com.runing.common.error;

/**
 * 커뮤니티 관련 에러들을 정의
 */
public enum CommunityErrorCode implements ErrorCode {

	;

	private final String code;
	private final String message;

	CommunityErrorCode(String code, String message) {
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
