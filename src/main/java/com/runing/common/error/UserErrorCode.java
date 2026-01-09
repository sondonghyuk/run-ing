package com.runing.common.error;

import org.springframework.http.HttpStatus;

/**
 * 사용자 관련 에러들을 정의
 */
public enum UserErrorCode implements ErrorCode {
	USER_EMAIL_EXISTS(HttpStatus.BAD_REQUEST,"U001","이미 존재하는 이메일 입니다."),
	USER_NICKNAME_EXISTS(HttpStatus.BAD_REQUEST,"U002","이미 존재하는 닉네임 입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND,"U003","Username 을 찾을 수 없습니다."),
	PROFILE_CANNOT_NULL(HttpStatus.BAD_REQUEST,"U004","Profile 은 null 이 될 수 없습니다."),
	USER_ALREADY_WITHDRAW(HttpStatus.BAD_REQUEST,"U005","해당 User 은 이미 비활성화 상태 입니다."),
	PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND,"U006","Profile 을 찾을 수 없습니다."),
	EMAIL_SEND_FAIL(HttpStatus.SERVICE_UNAVAILABLE,"U007","메일 전송에 실패했습니다."),
	EMAIL_SEND_TOO_FREQUENT(HttpStatus.BAD_REQUEST,"U008","메일 전송한지 1분이 지나지 않았습니다."),
	EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST,"U009","이미 인증된 이메일입니다."),
	;

	private final HttpStatus status;
	private final String code;
	private final String message;

	UserErrorCode(HttpStatus status, String code, String message) {
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
