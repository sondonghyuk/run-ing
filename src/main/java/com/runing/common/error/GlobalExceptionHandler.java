package com.runing.common.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.runing.common.response.ApiResponse;

/**
 * @RestController 로 선언한 지점에서 발생한 에러를 도중에
 * @RestControllerAdvice 로 선언한 클래스 내에서 캐치하여
 * Controller 내에서 발생한 에러를 처리
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * 공통 예외 처리
	 * BaseException 처리
	 */
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex){
		return ResponseEntity
			.status(ex.getErrorCode().getStatus())
			.body(ApiResponse.fail(ex.getErrorCode()));
	}
}
