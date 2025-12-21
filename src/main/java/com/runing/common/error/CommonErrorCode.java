package com.runing.common.error;

import org.springframework.http.HttpStatus;
/**
 * 공통 에러들을 정의
 */
public enum CommonErrorCode implements ErrorCode {
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IS001", "INTERNAL_SERVER_ERROR");
	;


	private final HttpStatus status;
	private final String code;
	private final String message;

	CommonErrorCode(HttpStatus status, String code, String message) {
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
