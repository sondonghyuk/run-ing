package com.runing.common.response;

import lombok.Getter;

/**
 * 에러 발생 시 클라이언트에게 반환되는 공통 에러 응답 객체
 */
@Getter
public class ErrorResponse{
	private int status;
	private String code;
	private String message;

	public ErrorResponse(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}