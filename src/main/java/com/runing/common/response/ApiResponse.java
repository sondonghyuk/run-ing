package com.runing.common.response;

import com.runing.common.error.ErrorCode;

import lombok.Getter;

/**
 * 클라이언트에서 요청한 값에 대해 성공,실패 응답 반환 값
 */
@Getter
public class ApiResponse<T> {
	private final boolean success;
	private final String code;
	private final String message;
	private final T data;

	public ApiResponse(boolean success, String code, String message, T data) {
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	//성공 응답
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, null, null, data);
	}

	//실패 응답
	public static ApiResponse<?> fail(ErrorCode errorCode) {
		return new ApiResponse<>(false,errorCode.getCode(),errorCode.getMessage(),null	);
	}

}
